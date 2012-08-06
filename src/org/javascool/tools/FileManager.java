/*
 * $file.name
 *     Copyright (C) 2012  Philippe VIENNE
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.javascool.tools;

// Used for URL formation

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// Used for URL read
// Used for URL write
// Used for list/exists

/**
 * Met à disposition des fonctions de gestion de fichiers locaux et distants.
 * <p>Lit/Ecrit un contenu textuel local ou distant en tenant compte de l'encodage local.</p>
 *
 * @serial exclude
 * @see <a href="FileManager.java.html">code source</a>
 */
public class FileManager {
    // @factory

    private FileManager() {
    }

    /**
     * Lit un contenu textuel local ou distant en tenant compte de l'encodage local.
     *
     * @param location Une URL (Universal Resource Location) de la forme: <div id="load-format"><table align="center">
     *                 <tr><td><tt>http:/<i>path-name</i></tt></td><td>pour aller chercher le contenu sur un site web</td></tr>
     *                 <tr><td><tt>http:/<i>path-name</i>?param_i=value_i&amp;..</tt></td><td>pour le récupérer sous forme de requête HTTP</td></tr>
     *                 <tr><td><tt>file:/<i>path-name</i></tt></td><td>pour le charger du système de fichier local ou en tant que ressource Java dans le CLASSPATH</td></tr>
     *                 <tr><td><tt>jar:/<i>jar-path-name</i>!/<i>jar-entry</i></tt></td><td>pour le charger d'une archive
     *                 <div>(exemple:<tt>jar:http://javascool.gforge.inria.fr/javascool.jar!/META-INF/MANIFEST.MF</tt>)</div></td></tr>
     *                 </table></div>
     * @param utf8     Si la valeur est vraie, force l'encodage en UTF-8 à la lecture. Par défaut (false) utilise l'encodage local.
     * @throws IllegalArgumentException Si l'URL est mal formée.
     * @throws RuntimeException         Si une erreur d'entrée-sortie s'est produite.
     */
    public static String load(String location, boolean utf8) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(FileManager.getResourceURL(location, true).openStream(),
                    utf8 ? Charset.forName("utf-8") : Charset.defaultCharset()), 10240);
            StringBuilder buffer = new StringBuilder();
            char chars[] = new char[10240];
            while (true) {
                int l = reader.read(chars);
                if (l == -1) {
                    break;
                }
                buffer.append(chars, 0, l);
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e + " when loading: " + location);
        }
    }

    /**
     * * @see #load(String, boolean)
     */
    public static String load(String location) {
        return load(location, false);
    }

    /**
     * Ecrit un contenu textuel local ou distant en tenant compte de l'encodage local.
     *
     * @param location Une URL (Universal Resource Location) de la forme: <div id="save-format"><table>
     *                 <tr><td><tt>ftp:/<i>path-name</i></tt></td><td>pour sauver sur un site FTP.</td></tr>
     *                 <tr><td><tt>file:/<i>path-name</i></tt></td><td>pour sauver dans le système de fichier local (le <tt>file:</tt> est optionnel).</td></tr>
     *                 <tr><td><tt>mailto:<i>address</i>?subject=<i>subject</i></tt></td><td>pour envoyer un courriel avec le texte en contenu.</td></tr>
     *                 <tr><td><tt>stdout:/</tt></td><td>pour l'imprimer dans la console.</td></tr>
     *                 </table></div>
     * @param string   Le texte à sauvegarder.
     * @param backup   Si true, dans le cas d'un fichier, crée une sauvegarde d'un fichier existant. Par défaut false.
     *                 * <p>Le fichier sauvegardé est doté d'un suffixe numérique unique.</p>
     * @param utf8     Si la valeur est vraie, force l'encodage en UTF-8 à la lecture. Par défaut (false) utilise l'encodage local.
     * @throws IllegalArgumentException Si l'URL est mal formée.
     * @throws RuntimeException         Si une erreur d'entrée-sortie s'est produite.
     */
    public static void save(String location, String string, boolean backup, boolean utf8) {
        if (location.startsWith("stdout:")) {
            System.out.println("\n" + location + " " + string);
            return;
        }
        location = FileManager.getResourceURL(location, false).toString();
        try {
            if (location.startsWith("file:") && (new File(location.substring(5)).getParentFile() != null)) {
                new File(location.substring(5)).getParentFile().mkdirs();
            }
            if (backup && !location.startsWith("file:")) {
                throw new IllegalArgumentException("Impossible de procéder à un backup pour l'URL «" + location + "»");
            }
            OutputStreamWriter writer = location.startsWith("file:") ? getFileWriter(location.substring(5), backup, utf8) : getUrlWriter(location, utf8);
            for (int i = 0; i < string.length(); i++)
                writer.write(string.charAt(i));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e + " when saving: " + location);
        }
    }

    /**
     * @see #save(String, String, boolean, boolean)
     */
    public static void save(String location, String string, boolean backup) {
        save(location, string, backup, false);
    }

    /**
     * @see #save(String, String, boolean, boolean)
     */
    public static void save(String location, String string) {
        save(location, string, false, false);
    }

    /**
     * Met en place le writer dans le cas d'une URL.
     */
    private static OutputStreamWriter getUrlWriter(String location, boolean utf8) throws IOException {
        URL url = new URL(location);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),
                utf8 ? Charset.forName("utf-8") : Charset.defaultCharset());
        if (url.getProtocol().equals("mailto")) {
            int i = url.toString().indexOf("?subject=");
            if (i != -1) {
                writer.write("Subject: " + url.toString().substring(i + 9) + "\n");
            }
        }
        return writer;
    }

    /**
     * Mets en place le writer dans le cas d'un fichier.
     */
    private static OutputStreamWriter getFileWriter(String location, boolean backup, boolean utf8) throws IOException {
        File file = new File(location), parent = file.getParentFile();
        if ((parent != null) && (!parent.isDirectory())) {
            parent.mkdirs();
        }
        if (backup && file.exists()) {
            backup(file);
        }
        return new OutputStreamWriter(new FileOutputStream(location), utf8 ? Charset.forName("utf-8") : Charset.defaultCharset());
    }

    /**
     * Mécanisme de backup.
     */
    private static void backup(File file) {
        File backup = new File(file.getAbsolutePath() + "~");
        if (backup.exists()) {
            backup(backup);
        }
        file.renameTo(backup);
    }

    /**
     * Détecte si une URL existe.
     *
     * @param location Une URL (Universal Resource Location).
     * @return Renvoie true si l'URL existe et est lisible, false sinon.
     */
    public static boolean exists(String location) {
        if (location.matches("(ftp|http|https|jar):.*")) {
            try {
                return exists(new URL(location));
            } catch (IOException e) {
                return false;
            }
        } else {
            if (location.matches("file:.*")) {
                location = location.substring(5);
            }
            return new File(location).canRead();
        }
    }

    /**
     * @see #exists(String)
     */
    public static boolean exists(URL location) {
        try {
            location.openStream().close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Renvoie la date de dernière modification d'une URL si elle existe.
     *
     * @param location Une URL (Universal Resource Location)
     * @return La valeur du temps de dernière modification, donné en millisecondes depuis le 1er janvier 1970 en temps GMT, ou 0 si la valeur est indéfinie.
     */
    public static long getLastModified(String location) {
        location = FileManager.getResourceURL(location).toString();
        if (location.matches("(ftp|http|https|jar):.*")) {
            try {
                return new URL(location).openConnection().getLastModified();
            } catch (IOException e) {
                return 0;
            }
        } else {
            if (location.matches("file:.*")) {
                location = location.substring(5);
            }
            return new File(location).lastModified();
        }
    }

    /**
     * Renvoie les fichiers d'un répertoire ou d'un jar.
     *
     * @param folder  Le nom du répertoire ou du fichier jar (fichier d'extension ".jar").
     * @param pattern Une regex qui définit le type de fichier (ex : <tt>".*\.java"</tt>). Par défaut tous les fichiers.
     * @param depth   Dans le cas d'un répertoire, profondeur: 0 (défaut) pour lire dans le répertoire, 1: répertoire et sous-répertoire, etc..
     * @return Une énumération des fichiers listés: le path canonique est renvoyé. Si le répertoire ou le jar ne peut être lu, renvoie une liste vide dans erreur.
     * @throws IllegalArgumentException Si l'URL ne peut pas être listée.
     * @throws RuntimeException         Si une erreur d'entrée-sortie s'est produite.
     */
    public static String[] list(String folder, String pattern, int depth) {
        if (folder.matches("(ftp|http|https|jar):.*")) {
            throw new IllegalArgumentException("Impossible de lister le contenu d'un URL de ce type: " + folder);
        }
        if (folder.matches("file:.*")) {
            folder = folder.substring(5);
        }
        ArrayList<String> files = new ArrayList<String>();
        if (folder.matches(".*\\.jar")) {
            try {
                for (Enumeration<JarEntry> e = new JarFile(folder).entries(); e.hasMoreElements(); ) {
                    String file = e.nextElement().getName();
                    if ((pattern == null) || file.matches(pattern)) {
                        files.add("jar:" + folder + "!" + file);
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (new File(folder).isDirectory() && (depth >= 0)) {
            try {
                for (File file : new File(folder).listFiles())
                    if ((pattern == null) || file.getName().matches(pattern)) {
                        files.add(file.getCanonicalPath());
                    }
                if (depth > 0) {
                    for (File file : new File(folder).listFiles())
                        if (file.isDirectory()) {
                            files.addAll(Arrays.asList(list(file.getCanonicalPath(), pattern, depth - 1)));
                        }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return files.toArray(new String[files.size()]);
    }

    /**
     * @see #list(String, String, int)
     */
    public static String[] list(String folder, String pattern) {
        return list(folder, pattern, 0);
    }

    /**
     * @see #list(String, String, int)
     */
    public static String[] list(String folder) {
        return list(folder, null, 0);
    }

    /**
     * Crée un répertoire temporaire dans le répertoire temporaire de la machine.
     *
     * @param prefix Prefix du répertoire.
     * @throws RuntimeException Si une erreur d'entrée-sortie s'est produite.
     */
    public static File createTempDir(String prefix) {
        try {
            File d = File.createTempFile(prefix, "");
            d.delete();
            d.mkdirs();
            return d;
        } catch (IOException e) {
            throw new RuntimeException(e + " when creating temporary directory");
        }
    }

    /**
     * Renvoie une URL (Universal Resource Location) normalisée, dans le cas du système de fichier local ou d'une ressource.
     * <p>La fonction recherche l'existence du fichier:
     * (i) par rapport au répertoire de base qui est donné,
     * (ii) par rapport au dossier de travaul "user.dir",
     * (iii) par rapport à la racine des fichier "user.home",
     * (iv) dans les ressources du CLASSPATH.</p>
     *
     * @param location L'URL à normaliser.
     * @param base     Un répertoire de réference pour la normalisation. Par défaut null.
     * @param reading  Précise si nous sommes en lecture (true) ou écriture (false). Par défaut en lecture.
     * @throws IllegalArgumentException Si l'URL est mal formée.
     */
    public static URL getResourceURL(String location, String base, boolean reading) {
        if (base != null) {
            location = base + "/" + location;
        }
        try {
            // @patch : ceci blinde un bug sur les URL jar
            if (location.matches("jar:[^!]*!.*")) {
                String res = location.replaceFirst("[^!]*!/", "");
                URL url = Thread.currentThread().getContextClassLoader().getResource(res);
                if (url != null) {
                    return url;
                } else {
                    throw new IllegalArgumentException("Unable to find " + res + " from " + location + " as a classpath resource");
                }
            }
            if (location.matches("(ftp|http|https|jar|mailto|stdout):.*")) {
                return new URL(location).toURI().normalize().toURL();
            }
            if (location.startsWith("file:")) {
                location = location.substring(5);
            }
            if (reading) {
                File file = new File(location);
                if (file.exists()) {
                    return new URL("file:" + file.getCanonicalPath());
                }
                file = new File(System.getProperty("user.dir"), location);
                if (file.exists()) {
                    return new URL("file:" + file.getCanonicalPath());
                }
                file = new File(System.getProperty("user.home"), location);
                if (file.exists()) {
                    return new URL("file:" + file.getCanonicalPath());
                }
                URL url = Thread.currentThread().getContextClassLoader().getResource(location);
                if (url != null) {
                    return url;
                }
            }
            return new URL("file:" + location);
        } catch (IOException e) {
            throw new IllegalArgumentException(e + " : " + location + " is a malformed URL");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e + " : " + location + " is a malformed URL");
        }
    }

    /**
     * @see #getResourceURL(String, String, boolean)
     */
    public static URL getResourceURL(String location, String base) {
        return getResourceURL(location, base, true);
    }

    /**
     * @see #getResourceURL(String, String, boolean)
     */
    public static URL getResourceURL(String location, boolean reading) {
        return getResourceURL(location, null, reading);
    }

    /**
     * @see #getResourceURL(String, String, boolean)
     */
    public static URL getResourceURL(String location) {
        return getResourceURL(location, null, true);
    }
}

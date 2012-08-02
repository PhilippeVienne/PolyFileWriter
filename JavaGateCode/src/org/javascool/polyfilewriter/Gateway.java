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

package org.javascool.polyfilewriter;

import org.javascool.tools.FileManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.applet.Applet;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * JS-Java Gate for PolyFileWriter.
 *
 * @author Philippe VIENNE
 */
public class Gateway extends Applet {

    /* ================================================================================================================
    *  ================================================================================================================
    *
    *        API Section
    *             All public functions below are accessible from JS
    *
    *  ================================================================================================================
    *  ================================================================================================================
    */

    /**
     * Read a file on a location.
     * Replace the FileReader API for text files (code e.g.)
     *
     * @param location Where have I got to read your file ?
     * @return The file content
     * @see FileManager#load(String)
     */
    public String load(final String location) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        public String run() {
                            return FileManager.load(location);
                        }
                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Write a File on a location.
     * Replace the FileWriter API for text files only (code, plain text e.g.)
     *
     * @param location Where have I got to write your file ?
     * @param what     The content to write into the file
     * @return true if all is ok and the file is safe false otherwise
     * @see FileManager#save(String, String)
     */
    public boolean save(final String location, final String what) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<Boolean>(){
                        public Boolean run() {
                            try {
                                FileManager.save(location, what);
                                return true;
                            } catch (Exception e) {
                                return false;
                            }
                        }
                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Return the user's home directory.
     * Use the System.getProperty("user.home")
     *
     * @return The path to the directory
     */
    public String getHomeDirectory() throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        public String run() {
                            return System.getProperty("user.home");
                        }
                    }
            );

        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Create a new directory
     *
     * @param location The path of new folder
     * @return true On success false If the folder already exists
     */
    public boolean createDirectory(final String location) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<Boolean>() {
                        public Boolean run() {
                            try {
                                if (location != null) {
                                    if (new File(location).exists()) {
                                        throw new IllegalArgumentException("Directory " + location + " already exists");
                                    }
                                    new File(location).mkdirs();
                                } else {
                                    throw new IllegalArgumentException("location is null");
                                }
                            } catch (Exception e) {
                                return false;
                            }
                            return true;
                        }
                    }
            );

        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Remove a file or directory
     *
     * @param location The path to file or directory
     * @return true On success false If the file or folder don't exist
     */
    public boolean remove(final String location) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<Boolean>() {
                        public Boolean run() {
                            try {
                                if (location != null) {
                                    if (!new File(location).exists()) {
                                        throw new IllegalArgumentException("" + location + " does not exist");
                                    }
                                    new File(location).delete();
                                } else {
                                    throw new IllegalArgumentException("location is null");
                                }
                            } catch (Exception e) {
                                return false;
                            }
                            return true;
                        }
                    }
            );

        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Move a file or directory
     *
     * @param location The path to file or directory
     * @param to       The new path
     * @return true On success false If the file or folder don't exist
     */
    public boolean move(final String location, final String to) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<Boolean>() {
                        public Boolean run() {
                            try {
                                if (location != null) {
                                    if (!new File(location).exists() || new File(to).exists()) {
                                        throw new IllegalArgumentException("Can't move " + location + " because location path do not exist or something exists in the 'to' path");
                                    }
                                    new File(location).renameTo(new File(to));
                                } else {
                                    throw new IllegalArgumentException("location is null");
                                }
                            } catch (Exception e) {
                                return false;
                            }
                            return true;
                        }
                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * List files into a directory.
     *
     * @param location The directory to list
     * @return A string with a structure as this
     *         [
     *         {"name":"toto.java","isFile":true,"isHidden":false,"path":"/home/user/toto.java"},
     *         ]
     */
    public String listDirectory(final String location) throws Exception {
        assertSafeUsage();
        try {
            final File[] list = AccessController.doPrivileged(
                    new PrivilegedAction<File[]>() {
                        public File[] run() {
                            return new File(location).listFiles();
                        }
                    }
            );
            final JSONArray files=new JSONArray();
            for (int i = 0; i < list.length; i++) {
                final int c = i;
                files.add(AccessController.doPrivileged(
                        new PrivilegedAction<JSONObject>() {
                            public JSONObject run() {
                                JSONObject obj=new JSONObject();
                                obj.put("name",list[c].getName());
                                obj.put("path",list[c].getAbsolutePath());
                                obj.put("isHidden",list[c].isHidden());
                                obj.put("isFile",list[c].isFile()) ;
                                return obj;
                          }
                        }
                ));
            }
            return files.toJSONString();
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * @see #askFile(boolean, String, String)
     */
    public String askFile() throws Exception {
        return askFile(false);
    }

    /**
     * @see #askFile(boolean, String, String)
     */
    public String askFile(boolean forSave) throws Exception {
        return askFile(forSave, null);
    }

    /**
     * @see #askFile(boolean, String, String)
     */
    public String askFile(boolean forSave, String ext) throws Exception {
        return askFile(forSave, ext, this.getHomeDirectory());
    }

    /**
     * Open an AWT FileDialog.
     * Allow to ask to the user to ask any file to the user.
     *
     * @param forSave If it's true, the AWT Dialog will be opened in save mode. Otherwise, it will be opened in open
     *                mode
     * @param ext     Allowed extensions separated by spaces. Leave null or empty string to allow all extensions
     * @return The path of the selected file.
     */
    public String askFile(final boolean forSave, String ext, final String path) throws Exception {
        assertSafeUsage();
        try {
            if (ext == null) ext = "";
            try {
                final JFileChooser fc = AccessController.doPrivileged(
                        new PrivilegedAction<JFileChooser>() {
                            public JFileChooser run() {
                                return new JFileChooser(getFile(path));
                            }
                        }
                );
                return AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        int r = JFileChooser.ABORT;
                        if (forSave)
                            r = fc.showSaveDialog(null);
                        else
                            r = fc.showOpenDialog(null);
                        if (r == JFileChooser.APPROVE_OPTION) {
                            final File file = fc.getSelectedFile();
                            return file.getAbsolutePath();
                        } else
                            return null;
                    }
                });
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Pop the last Java exception which happened
     *
     * @return The Exception
     */
    public Exception popException() {
        if (lastError != null) {
            Exception e = lastError;
            lastError = null;
            return e;
        }
        return null;
    }

    private Exception lastError;

    private void popException(Exception e) {
        lastError = e;
    }

    /* ================================================================================================================
    *  ================================================================================================================
    *
    *        SECURITY SECTION
    *             All variables and code change below will affect the application's security
    *
    *  ================================================================================================================
    *  ================================================================================================================
    */

    /**
     * Create a File object from the Path.
     *
     * @param path
     * @return
     */
    private File getFile(final String path) {
        return AccessController.doPrivileged(
                new PrivilegedAction<File>() {
                    public File run() {
                        return new File(path);
                    }
                }
        );
    }

    /**
     * Security flag.
     * If the applet have to be locked for security reasons, put this variable to true.
     */
    private boolean appletLocked = false;

    /**
     * Initialize the applet.
     * This function will check if we are in a safe environment.
     * NOTE: You can edit it to adapt to your own environment
     */
    @Override
    public void init() {
        if (!getCodeBase().getProtocol().equals("file")) {
            appletLocked = true;
        }
    }

    /**
     * Message non-spam flag.
     * This flag is used to be sure that we show the security message only one time.
     */
    private boolean showMessage = true;

    /**
     * Perform a security assertion.
     * If the applet is locked, this function will interrupt the current thread.
     *
     * @throws SecurityException
     */
    private void assertSafeUsage() {
        if (appletLocked) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "This website (" + getCodeBase().getHost() + ") tried to hack" +
                        " your computer by accessing to the local file system (Attack stopped)", "Error",
                        JOptionPane.ERROR_MESSAGE);
                showMessage = false;
            }
            throw new SecurityException("This website is not authorized to use this applet");
        } else {
        }
    }

}

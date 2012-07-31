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

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/** JS-Java Gate for PolyFileWriter.
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

    /** Read a file on a location.
     * Replace the FileReader API for text files (code e.g.)
     * @see FileManager#load(String)
     * @param location Where have I got to read your file ?
     * @return The file content
     */
    public String load(final String location) {
        assertSafeUsage();
        return (String) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return FileManager.load(location);
                    }
                }
        );
    }

    /** Write a File on a location.
     * Replace the FileWriter API for text files only (code, plain text e.g.)
     * @see FileManager#save(String, String)
     * @param location Where have I got to write your file ?
     * @param what The content to write into the file
     * @return true if all is ok and the file is safe false otherwise
     */
    public boolean save(final String location, final String what) {
        assertSafeUsage();
        return (Boolean) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        try {
                            FileManager.save(location, what);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                }
        );
    }

    /** Return the user's home directory.
     * Use the System.getProperty("user.home")
     * @return The path to the directory
     */
    public String getHomeDirectory(){
        assertSafeUsage();
        return (String) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty("user.home");
                    }
                }
        );
    }

    /** List files into a directory.
     * @param location The directory to list
     * @return An array with a structure as this
     * [
     *      ["toto.java","file"],
     *      ["toto2.java","file"],
     *      ["plugin","directory"],
     * ]
     */
    public String[][] listDirectory(final String location){
        assertSafeUsage();
        File[] list=(File[]) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return new File(location).listFiles();
                    }
                }
        );
        String[][] returnList=new String[list.length][2];
        for (int i=0;i<list.length;i++){
            String[] rf=new String[2];
            rf[0]=list[i].getName();rf[1]=list[i].isFile()?"file":"directory";
            returnList[i]=rf;
        }
        return returnList;
    }

    /**
     * @see #askFile(boolean,String,String)
     */
    public String askFile() {
        return askFile(false);
    }

    /**
     * @see #askFile(boolean,String,String)
     */
    public String askFile(boolean forSave) {
        return askFile(forSave,null);
    }

    /**
     * @see #askFile(boolean,String,String)
     */
    public String askFile(boolean forSave,String ext) {
        return askFile(forSave,ext,this.getHomeDirectory());
    }

    /** Open an AWT FileDialog.
     * Allow to ask to the user to ask any file to the user.
     * @param forSave If it's true, the AWT Dialog will be opened in save mode. Otherwise, it will be opened in open
     *                mode
     * @param ext Allowed extensions separated by spaces. Leave null or empty string to allow all extensions
     * @return The path of the selected file.
     */
    public String askFile(boolean forSave, String ext, String path) {
        assertSafeUsage();
        if(ext==null)ext="";
        JFileChooser fc=new JFileChooser();
        fc.setCurrentDirectory(getFile(path));
        int r=JFileChooser.ABORT;
        if(forSave)
            fc.showSaveDialog(this);
        else
            fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        } else
            return "";
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

    /** Create a File object from the Path.
     *
     * @param path
     * @return
     */
    private File getFile(final String path){
        return (File) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return new File(path);
                    }
                }
        );
    }

    /** Security flag.
     * If the applet have to be locked for security reasons, put this variable to true.
     */
    private boolean appletLocked=false;

    /** Initialize the applet.
     * This function will check if we are in a safe environment.
     * NOTE: You can edit it to adapt to your own environment
     */
    @Override
    public void init(){
        if(!getCodeBase().getProtocol().equals("file")){
            appletLocked=true;
        }
    }

    /** Message non-spam flag.
     * This flag is used to be sure that we show the security message only one time.
     */
    private boolean showMessage=true;

    /** Perform a security assertion.
     * If the applet is locked, this function will interrupt the current thread.
     * @throws SecurityException
     */
    private void assertSafeUsage(){
        if(appletLocked){
            if(showMessage){
                JOptionPane.showMessageDialog(this, "This website ("+getCodeBase().getHost()+") tried to hack" +
                        " your computer by accessing to the local file system (Attack stopped)", "Error",
                        JOptionPane.ERROR_MESSAGE);
                showMessage=false;
            }
            throw new SecurityException("This website is not authorized to use this applet");
        }else{}
    }

}

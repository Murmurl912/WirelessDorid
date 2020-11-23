package com.example.wirlessdroid.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * This is the file abstraction used by the server.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface VirtualFile {

    /**
     * Get the full path from the base directory of the FileSystemView.
     * @return a path where the path separator is '/' (even if the operating system
     *     uses another character as path separator).
     */
    String getAbsolutePath();

    /**
     * Get the file name of the file
     * @return the last part of the file path (the part after the last '/').
     */
    String getName();

    /**
     * Is the file hidden?
     * @return true if the {@link VirtualFile} is hidden
     */
    boolean isHidden();

    /**
     * Is it a directory?
     * @return true if the {@link VirtualFile} is a directory
     */
    boolean isDirectory();

    /**
     * Is it a file?
     * @return true if the {@link VirtualFile} is a file, false if it is a directory
     */
    boolean isFile();

    /**
     * Does this file exists?
     * @return true if the {@link VirtualFile} exists
     */
    boolean doesExist();

    /**
     * Has read permission?
     * @return true if the {@link VirtualFile} is readable by the user
     */
    boolean isReadable();

    /**
     * Has write permission?
     * @return true if the {@link VirtualFile} is writable by the user
     */
    boolean isWritable();

    /**
     * Has delete permission?
     * @return true if the {@link VirtualFile} is removable by the user
     */
    boolean isRemovable();

    /**
     * Get the owner name.
     * @return The name of the owner of the {@link VirtualFile}
     */
    String getOwnerName();

    /**
     * Get owner group name.
     * @return The name of the group that owns the {@link VirtualFile}
     */
    String getGroupName();

    /**
     * Get last modified time in UTC.
     * @return The timestamp of the last modified time for the {@link VirtualFile}
     */
    long getLastModified();

    /**
     * Set the last modified time stamp of a file
     * @param time The last modified time, in milliseconds since the epoch. See {@link File#setLastModified(long)}.
     */
    boolean setLastModified(long time);

    /**
     * Get file size.
     * @return The size of the {@link VirtualFile} in bytes
     */
    long getSize();

    /**
     * Returns the physical location or path of the file. It is completely up to
     * the implementation to return appropriate value based on the file system
     * implementation.
     *
     * @return the physical location or path of the file.
     */
    Object getPhysicalFile();

    /**
     * Create directory.
     * @return true if the operation was successful
     */
    boolean mkdir();

    /**
     * Delete file.
     * @return true if the operation was successful
     */
    boolean delete();

    /**
     * Move file.
     * @param destination The target {@link VirtualFile} to move the current {@link VirtualFile} to
     * @return true if the operation was successful
     */
    boolean move(VirtualFile destination);

    /**
     * List file objects. If not a directory or does not exist, null will be
     * returned. Files must be returned in alphabetical order.
     * List must be immutable.
     * @return The {@link List} of {@link VirtualFile}s
     */
    List<? extends VirtualFile> listFiles();

    /**
     * Create output stream for writing.
     * @param offset The number of bytes at where to start writing.
     *      If the file is not random accessible,
     *      any offset other than zero will throw an exception.
     * @return An {@link OutputStream} used to write to the {@link VirtualFile}
     * @throws IOException io error occurs
     */
    OutputStream createOutputStream(long offset) throws IOException;

    /**
     * Create input stream for reading.
     * @param offset The number of bytes of where to start reading.
     *          If the file is not random accessible,
     *          any offset other than zero will throw an exception.
     * @return An {@link InputStream} used to read the {@link VirtualFile}
     * @throws IOException io error occurs
     */
    InputStream createInputStream(long offset) throws IOException;
}

package dev.doublekekse.super_mod.computer.file_system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class VirtualFile {
    private String filename;
    private VirtualFileSystem vfs;
    private byte[] data;
    private long pointer = 0;
    private boolean isTempFile = false;

    // Constructor for a persistent file in the virtual file system
    public VirtualFile(String filename, VirtualFileSystem vfs) {
        this.filename = filename;
        this.vfs = vfs;

        // Load file data if it exists, or create a new file
        if (vfs.fileExists(filename)) {
            data = vfs.readFile(filename);
        } else {
            data = new byte[0];
            vfs.createFile(filename, data);
        }
    }

    // Constructor for a temporary file that doesn't exist in the virtual file system
    public VirtualFile() {
        this.isTempFile = true;
        this.data = new byte[0]; // Temp file has no initial data
    }

    // Move the file pointer to a specific position
    public void seek(long pos) {
        if (pos < 0 || pos > data.length) {
            throw new IndexOutOfBoundsException("Seek position out of bounds");
        }
        pointer = pos;
    }

    // Appends to the file by moving the pointer to the end
    public void append() {
        pointer = data.length;
    }

    // Returns the length of the file
    public long length() {
        return data.length;
    }

    // Sets the length of the file, truncating or extending as needed
    public void setLength(long length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }
        if (length < data.length) {
            data = Arrays.copyOf(data, (int) length); // Truncate file
        } else {
            byte[] newData = new byte[(int) length];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData; // Extend file with zeroes
        }
        if (!isTempFile) {
            vfs.createFile(filename, data); // Update file in VFS
        }
        if (pointer > length) {
            pointer = length; // Adjust pointer if it exceeds new length
        }
    }

    // Closes the file and commits changes to the virtual file system if needed
    public void close() {
        if (!isTempFile) {
            vfs.createFile(filename, data); // Save file in VFS
        } else {
            data = null; // Clear data for temporary files
        }
    }

    // Writes len bytes from the specified byte array starting at offset off
    public void write(byte[] b, int off, int len) {
        if (b == null || off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("Invalid offset or length");
        }

        if (pointer + len > data.length) {
            setLength(pointer + len); // Expand file if necessary
        }

        System.arraycopy(b, off, data, (int) pointer, len);
        pointer += len;
        if (!isTempFile) {
            vfs.createFile(filename, data); // Update file in VFS
        }
    }

    // Reads a single byte from the file
    public int read() {
        if (pointer >= data.length) {
            return -1; // End of file
        }
        return data[(int) pointer++] & 0xFF; // Return unsigned byte
    }

    // Reads len bytes into the specified byte array starting at offset off
    public int read(byte[] b, int off, int len) {
        if (b == null || off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("Invalid offset or length");
        }

        if (pointer >= data.length) {
            return -1; // End of file
        }

        int bytesToRead = Math.min(len, (int) (data.length - pointer));
        System.arraycopy(data, (int) pointer, b, off, bytesToRead);
        pointer += bytesToRead;
        return bytesToRead;
    }

    public String readAllToString() throws IOException {
        long length = length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large to be read as a single string");
        }

        byte[] buffer = new byte[(int) length];
        read(buffer, 0, (int) length);
        return new String(buffer);
    }

    // Returns the current file pointer position
    public long getFilePointer() {
        return pointer;
    }

    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                byte[] singleByte = {(byte) b};
                write(singleByte, 0, 1);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                VirtualFile.this.write(b, off, len);
            }

            @Override
            public void close() throws IOException {
                VirtualFile.this.close();
            }
        };
    }

    public InputStream getInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return VirtualFile.this.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return VirtualFile.this.read(b, off, len);
            }

            @Override
            public void close() throws IOException {
                VirtualFile.this.close();
            }
        };
    }
}

package dev.doublekekse.super_mod.luaj.lib;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import dev.doublekekse.super_mod.computer.file_system.VirtualFile;
import dev.doublekekse.super_mod.luaj.LuaComputer;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.IoLib;

public class PiOsIoLib extends IoLib {
    final LuaComputer<?> lc;

    public PiOsIoLib(LuaComputer<?> lc) {
        this.lc = lc;
    }

    protected File wrapStdin() {
        return new StdinFile();
    }

    protected File wrapStdout() {
        return new StdoutFile(FTYPE_STDOUT);
    }

    protected File wrapStderr() {
        return new StdoutFile(FTYPE_STDERR);
    }

    protected File openFile(String filename, boolean readMode, boolean appendMode, boolean updateMode, boolean binaryMode) throws IOException {
        if (readMode && !lc.getVfs().fileExists(filename)) {
            throw new IOException();
        }

        VirtualFile f = new VirtualFile(filename, lc.getVfs());

        if (appendMode) {
            f.seek(f.length());
        } else {
            if (!readMode) {
                f.setLength(0);
            }
        }
        return new FileImpl(f);
    }

    protected File openProgram(String prog, String mode) throws IOException {
        lc.openProgram(prog, null, false);

        return "w".equals(mode) ?
            new FileImpl(globals.STDOUT) :
            new FileImpl(globals.STDIN);
    }

    protected File tmpFile() {
        return new FileImpl(new VirtualFile());
    }

    private static void notImplemented() {
        throw new LuaError("not implemented");
    }


    private final class FileImpl extends File {
        private final VirtualFile file;
        private final InputStream is;
        private final OutputStream os;
        private boolean closed = false;
        private boolean nobuffer = false;

        private FileImpl(VirtualFile file, InputStream is, OutputStream os) {
            this.file = file;
            this.is = is != null ? is.markSupported() ? is : new BufferedInputStream(is) : null;
            this.os = os;
        }

        private FileImpl(VirtualFile f) {
            this(f, null, null);
        }

        private FileImpl(InputStream i) {
            this(null, i, null);
        }

        private FileImpl(OutputStream o) {
            this(null, null, o);
        }

        public String tojstring() {
            return "file (" + (this.closed ? "closed" : String.valueOf(this.hashCode())) + ")";
        }

        public boolean isstdfile() {
            return file == null;
        }

        public void close() {
            closed = true;
            if (file != null) {
                file.close();
            }
        }

        public void flush() throws IOException {
            if (os != null)
                os.flush();
        }

        public void write(LuaString s) throws IOException {
            if (os != null)
                os.write(s.m_bytes, s.m_offset, s.m_length);
            else if (file != null)
                file.write(s.m_bytes, s.m_offset, s.m_length);
            else
                notImplemented();
            if (nobuffer)
                flush();
        }

        public boolean isclosed() {
            return closed;
        }

        public int seek(String option, int pos) {
            if (file != null) {
                if ("set".equals(option)) {
                    file.seek(pos);
                } else if ("end".equals(option)) {
                    file.seek(file.length() + pos);
                } else {
                    file.seek(file.getFilePointer() + pos);
                }
                return (int) file.getFilePointer();
            }
            notImplemented();
            return 0;
        }

        public void setvbuf(String mode, int size) {
            nobuffer = "no".equals(mode);
        }

        // get length remaining to read
        public int remaining() {
            return file != null ? (int) (file.length() - file.getFilePointer()) : -1;
        }

        // peek ahead one character
        public int peek() throws IOException {
            if (is != null) {
                is.mark(1);
                int c = is.read();
                is.reset();
                return c;
            } else if (file != null) {
                long fp = file.getFilePointer();
                int c = file.read();
                file.seek(fp);
                return c;
            }
            notImplemented();
            return 0;
        }

        // return char if read, -1 if eof, throw IOException on other exception
        public int read() throws IOException {
            if (is != null)
                return is.read();
            else if (file != null) {
                return file.read();
            }
            notImplemented();
            return 0;
        }

        // return number of bytes read if positive, -1 if eof, throws IOException
        public int read(byte[] bytes, int offset, int length) throws IOException {
            if (file != null) {
                return file.read(bytes, offset, length);
            } else if (is != null) {
                return is.read(bytes, offset, length);
            } else {
                notImplemented();
            }
            return length;
        }
    }

    private final class StdoutFile extends File {
        private final int file_type;

        private StdoutFile(int file_type) {
            this.file_type = file_type;
        }

        public String tojstring() {
            return "file (" + this.hashCode() + ")";
        }

        private PrintStream getPrintStream() {
            return file_type == FTYPE_STDERR ?
                globals.STDERR :
                globals.STDOUT;
        }

        public void write(LuaString string) {
            getPrintStream().write(string.m_bytes, string.m_offset, string.m_length);
        }

        public void flush() {
            getPrintStream().flush();
        }

        public boolean isstdfile() {
            return true;
        }

        public void close() {
            // do not close std files.
        }

        public boolean isclosed() {
            return false;
        }

        public int seek(String option, int bytecount) {
            return 0;
        }

        public void setvbuf(String mode, int size) {
        }

        public int remaining() {
            return 0;
        }

        public int peek() {
            return 0;
        }

        public int read() {
            return 0;
        }

        public int read(byte[] bytes, int offset, int length) {
            return 0;
        }
    }

    private final class StdinFile extends File {
        private StdinFile() {
        }

        public String tojstring() {
            return "file (" + this.hashCode() + ")";
        }

        public void write(LuaString string) {
        }

        public void flush() {
        }

        public boolean isstdfile() {
            return true;
        }

        public void close() {
            // do not close std files.
        }

        public boolean isclosed() {
            return false;
        }

        public int seek(String option, int bytecount) {
            return 0;
        }

        public void setvbuf(String mode, int size) {
        }

        public int remaining() {
            return -1;
        }

        public int peek() throws IOException {
            globals.STDIN.mark(1);
            int c = globals.STDIN.read();
            globals.STDIN.reset();
            return c;
        }

        public int read() throws IOException {
            return globals.STDIN.read();
        }

        public int read(byte[] bytes, int offset, int length)
            throws IOException {
            return globals.STDIN.read(bytes, offset, length);
        }
    }
}

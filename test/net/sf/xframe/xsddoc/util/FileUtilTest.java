package net.sf.xframe.xsddoc.util;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileUtilTest {
    private void setFs(Configuration configuration) {
        Arrays.stream(FileSystems.class.getDeclaredClasses()).filter((cls) -> cls.getName().equals("java.nio.file.FileSystems$DefaultFileSystemHolder")).forEach((cls) ->{
            try {
                Field dfs = cls.getDeclaredField("defaultFileSystem");
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(dfs, dfs.getModifiers() & ~Modifier.FINAL);
                dfs.setAccessible(true);
                dfs.set(null, Jimfs.newFileSystem(configuration));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testFullFilePathWindows() {
        setFs(Configuration.windows());
        String filepath = "C:\\foo\\bar\\baz.zoot";
        String basepath = "C:\\foo\\bar\\";
        assertEquals(filepath, FileUtil.getLocation(basepath, filepath));
    }

    @Test
    public void testRelativeFilePathWindows() {
        setFs(Configuration.windows());
        String filepath = "bar\\baz.zoot";
        String basepath = "C:\\foo\\bar\\";
        assertEquals("C:\\foo\\bar\\baz.zoot", FileUtil.getLocation(basepath, filepath));
    }

    @Test
    public void testFullFilePathLinux() {
        setFs(Configuration.unix());
        String filepath = "/foo/bar/baz.zoot";
        String basepath = "/foo/bar/";
        assertEquals(filepath, FileUtil.getLocation(basepath, filepath));
    }

    @Test
    public void testRelativeFilePathLinux() {
        setFs(Configuration.unix());
        String filepath = "bar/baz.zoot";
        String basepath = "/foo/bar/";
        assertEquals("/foo/bar/baz.zoot", FileUtil.getLocation(basepath, filepath));
    }

    @Test
    public void testNullBasePathWindows() {
        setFs(Configuration.windows());
        String filepath = "hubba\\bubba";
        assertEquals(filepath, FileUtil.getLocation(null, filepath));
    }

    @Test
    public void testNullBasePathLinux() {
        setFs(Configuration.unix());
        String filepath = "hubba/bubba";
        assertEquals(filepath, FileUtil.getLocation(null, filepath));
    }
}
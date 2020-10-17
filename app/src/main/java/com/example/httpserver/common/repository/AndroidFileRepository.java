package com.example.httpserver.common.repository;

import android.content.ContentResolver;
import android.provider.DocumentsContract;
import androidx.documentfile.provider.DocumentFile;
import com.example.httpserver.common.model.FileMetaData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class AndroidFileRepository implements FileRepository {

    private FileRepository repository;
    private ContentResolver resolver;

    public AndroidFileRepository() {
        repository = new FileRepositoryImpl();
    }

    @Override
    public FileMetaData move(Path path, Path destination, boolean override) throws FileNotFoundException {
        try {
            return repository.move(path, destination, override);
        } catch (Exception e) {
            DocumentFile sourceDoc = DocumentFile.fromFile(path.toFile());
            DocumentFile desDoc = DocumentFile.fromFile(destination.toFile());
            DocumentsContract.moveDocument(resolver,
                    sourceDoc.getUri(),
                    Objects.requireNonNull(sourceDoc.getParentFile()).getUri(),
                    Objects.requireNonNull(desDoc.getParentFile()).getUri());
            return FileMetaData.from(null, destination);
        }
    }

    @Override
    public void remove(Path path, boolean recursive) throws IOException, SecurityException {
        repository.remove(path, recursive);
    }

    @Override
    public FileMetaData copy(Path path, Path destination, boolean override) throws IOException, SecurityException {
        return repository.copy(path, destination, override);
    }

    @Override
    public FileMetaData mkdir(Path path) throws IOException, SecurityException {
        return repository.mkdir(path);
    }

    @Override
    public FileMetaData touch(Path path) throws IOException, SecurityException {
        return repository.touch(path);
    }

    @Override
    public FileMetaData write(Path path, InputStream stream, boolean override) throws IOException, SecurityException {
        return repository.write(path, stream, override);
    }

    @Override
    public FileMetaData read(Path path, OutputStream stream) throws IOException, SecurityException {
        return repository.read(path, stream);
    }

    @Override
    public InputStream read(Path path) throws IOException {
        return repository.read(path);
    }

    @Override
    public FileMetaData meta(String uri, Path path) throws FileNotFoundException {
        return repository.meta(uri, path);
    }

    @Override
    public List<FileMetaData> dir(String uri, Path path) throws FileNotFoundException {
        return repository.dir(uri, path);
    }

    private static class FileRepositoryImpl implements FileRepository {

    }
}

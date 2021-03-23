package org.odk.collect.android.forms;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nullable;

public interface FormsRepository {
    @Nullable
    Form get(Long id);

    @Nullable
    Form getLatestByFormIdAndVersion(String formId, @Nullable String version);

    @Nullable
    Form getOneByPath(String path);

    @Nullable
    Form getOneByMd5Hash(@NotNull String hash);

    List<Form> getAll();

    List<Form> getAllByFormIdAndVersion(String formId, @Nullable String version);

    List<Form> getAllNotDeletedByFormId(String formId);

    List<Form> getAllNotDeletedByFormIdAndVersion(String formId, @Nullable String version);

    Form save(Form form);

    void delete(Long id);

    void softDelete(Long id);

    void deleteByMd5Hash(@NotNull String md5Hash);

    void restore(Long id);
}

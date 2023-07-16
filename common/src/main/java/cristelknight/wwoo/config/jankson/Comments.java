package cristelknight.wwoo.config.jankson;

import org.jetbrains.annotations.Nullable;

public interface Comments {

    void addComment(String key, String comment);

    @Nullable
    String getComment(String key);
}

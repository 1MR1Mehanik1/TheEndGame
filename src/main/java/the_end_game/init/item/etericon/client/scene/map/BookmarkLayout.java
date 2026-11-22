package the_end_game.init.item.etericon.client.scene.map;

public record BookmarkLayout(
        int bookmarkX,
        int bookmarkY,
        int bookmarkWidth,
        int bookmarkHeight,
        int frameX,
        int frameY,
        int frameWidth,
        int frameHeight,
        int imageX,
        int imageY,
        int imageWidth,
        int imageHeight,
        int frameGapLeft,
        int frameGapRight,
        int sideOffsetY,
        float leftScale,
        float rightScale,
        int bookmarkTop,
        int bookmarkBottom,
        int bookmarkGap,
        int stripMargin,
        int paperOverlap,
        int hoverOffset,
        int pageArrowY,
        int pageArrowOffsetLeft,
        int pageArrowOffsetRight,
        int pageArrowWidth,
        int pageArrowHeight
) {
    public static final BookmarkLayout DEFAULT = new BookmarkLayout(
            0,
            0,
            70,
            18,

            38,
            0,
            18,
            18,

            40,
            2,
            14,
            14,

            0,
            0,
            -1,
            0.80F,
            0.80F,

            11,
            173,
            0,
            8,
            35,
            4,

            158,
            -120,
            102,
            16,
            10
    );
}

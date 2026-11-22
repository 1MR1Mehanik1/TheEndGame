package the_end_game.init.item.etericon.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class Multiblock implements IMultiblock {
    private final ResourceLocation name;
    private final char[][][] rawPattern;
    private final Map<BlockPos, Matcher> matchers;
    private final int width;
    private final int height;
    private final int depth;
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;

    public Multiblock(ResourceLocation name, String[][] pattern, Map<Character, Matcher> definitions) { this(name, toLayers(pattern), definitions); }
    public Multiblock(ResourceLocation name, List<List<String>> layers, Map<Character, Matcher> definitions) {
        if (layers == null || layers.isEmpty()) { throw new IllegalArgumentException("Multiblock " + name + " has no layers"); }
        this.name = name;
        this.height = layers.size();
        int rows = layers.get(0).size();
        if (rows == 0) { throw new IllegalArgumentException("Multiblock " + name + " has an empty layer"); }
        int columns = layers.get(0).get(0).length();
        if (columns == 0) { throw new IllegalArgumentException("Multiblock " + name + " has an empty row"); }
        this.width = columns;
        this.depth = rows;
        this.rawPattern = new char[this.width][this.height][this.depth];
        int anchorX = 0;
        int anchorY = 0;
        int anchorZ = 0;
        for (int layer = 0; layer < layers.size(); layer++) {
            List<String> rowsInLayer = layers.get(layer);
            if (rowsInLayer.size() != this.depth) { throw new IllegalArgumentException("Multiblock " + name + " has inconsistent layer heights"); }
            for (int row = 0; row < rowsInLayer.size(); row++) {
                String line = rowsInLayer.get(row);
                if (line.length() != this.width) { throw new IllegalArgumentException("Multiblock " + name + " has inconsistent row widths"); }
                for (int column = 0; column < line.length(); column++) {
                    char symbol = line.charAt(column);
                    int y = this.height - 1 - layer;
                    this.rawPattern[column][y][row] = symbol;
                    if (symbol == '0') {
                        anchorX = column;
                        anchorY = y;
                        anchorZ = row;
                    }
                }
            }
        }
        this.xOffset = anchorX;
        this.yOffset = anchorY;
        this.zOffset = anchorZ;
        Map<BlockPos, Matcher> resolved = new LinkedHashMap<>();
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                for (int z = 0; z < this.depth; z++) {
                    char symbol = this.rawPattern[x][y][z];
                    Matcher matcher = definitions.get(symbol);
                    if (matcher == null) { throw new IllegalArgumentException("Multiblock " + name + " has no matcher for '" + symbol + "'"); }
                    if (!matcher.isWildcard()) { resolved.put(new BlockPos(x, y, z), matcher); }
                }
            }
        }
        this.matchers = Collections.unmodifiableMap(resolved);
    }

    private static List<List<String>> toLayers(String[][] pattern) { return Arrays.stream(pattern).map(Arrays::asList).toList(); }

    @Override public boolean isComplete(Level level, BlockPos center) { return isComplete(level, center, Rotation.NONE, Mirror.NONE); }

    public boolean isComplete(Level level, BlockPos center, Rotation rotation, Mirror mirror) {
        BlockPos start = getStart(center, rotation, mirror);
        for (Map.Entry<BlockPos, Matcher> entry : this.matchers.entrySet()) {
            BlockPos offset = entry.getKey();
            int[] transformed = transform(offset.getX(), offset.getZ(), rotation, mirror);
            BlockPos pos = start.offset(transformed[0], offset.getY(), transformed[1]);
            Matcher matcher = entry.getValue().mirror(mirror).rotate(rotation);
            if (!matcher.matches(level.getBlockState(pos))) { return false; }
        }
        return true;
    }

    @Override public boolean forEach(BlockPos center, char symbol, BiFunction<BlockPos, Matcher, Boolean> function) { return forEach(center, symbol, Rotation.NONE, Mirror.NONE, function); }

    public boolean forEach(BlockPos center, char symbol, Rotation rotation, Mirror mirror, BiFunction<BlockPos, Matcher, Boolean> function) {
        BlockPos start = getStart(center, rotation, mirror);
        for (Map.Entry<BlockPos, Matcher> entry : this.matchers.entrySet()) {
            BlockPos offset = entry.getKey();
            if (symbol != 0 && getChar(offset) != symbol) { continue; }
            int[] transformed = transform(offset.getX(), offset.getZ(), rotation, mirror);
            BlockPos pos = start.offset(transformed[0], offset.getY(), transformed[1]);
            if (!function.apply(pos, entry.getValue().mirror(mirror).rotate(rotation))) { return false; }
        }
        return true;
    }

    @Override public BlockPos getStart(BlockPos center) {
        return getStart(center, Rotation.NONE, Mirror.NONE);
    }

    public BlockPos getStart(BlockPos center, Rotation rotation, Mirror mirror) {
        int[] anchor = transform(this.xOffset, this.zOffset, rotation, mirror);
        return center.offset(-anchor[0], -this.yOffset, -anchor[1]);
    }

    @Override public char getChar(BlockPos offset) {
        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();
        if (x < 0 || x >= this.width || y < 0 || y >= this.height || z < 0 || z >= this.depth) { return 0; }
        return this.rawPattern[x][y][z];
    }

    public char getChar(int x, int y, int z) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height || z < 0 || z >= this.depth) { return 0; }
        return this.rawPattern[x][y][z];
    }

    public int getRotatedWidth(Rotation rotation) { return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90 ? this.depth : this.width; }
    public int getRotatedDepth(Rotation rotation) { return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90 ? this.width : this.depth; }

    public int[] transform(int x, int z, Rotation rotation, Mirror mirror) {
        int mirroredX = mirror == Mirror.LEFT_RIGHT ? this.width - 1 - x : x;
        int mirroredZ = mirror == Mirror.FRONT_BACK ? this.depth - 1 - z : z;
        return switch (rotation) {
            case NONE -> new int[]{mirroredX, mirroredZ};
            case CLOCKWISE_90 -> new int[]{this.depth - 1 - mirroredZ, mirroredX};
            case CLOCKWISE_180 -> new int[]{this.width - 1 - mirroredX, this.depth - 1 - mirroredZ};
            case COUNTERCLOCKWISE_90 -> new int[]{mirroredZ, this.width - 1 - mirroredX};
        };
    }

    @Override public ResourceLocation getName() { return this.name; }
    @Override public Map<BlockPos, Matcher> getMatchers() { return this.matchers; }
    @Override public int getWidth() { return this.width; }
    @Override public int getHeight() { return this.height; }
    @Override public int getDepth() { return this.depth; }
    @Override public int getXOffset() { return this.xOffset; }
    @Override public int getYOffset() { return this.yOffset; }
    @Override public int getZOffset() { return this.zOffset; }
    @Override public char[][][] getRawPattern() { return this.rawPattern.clone(); }
}

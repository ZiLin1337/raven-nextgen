package keystrokesmod.utility;


import net.minecraft.block.BlockState;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;

public final class StairsUtils {

    public interface BlockFaceDrawer {
        void draw(Box box, Direction face, int overlayStart, int overlayEnd, int outlineStart, int outlineEnd, boolean overlay, boolean outline);
    }

    public static void drawStairs(BlockPos blockPos, BlockState blockState, Box box, Direction side, double viewerX, double viewerY, double viewerZ, int overlayStartColor, int overlayEndColor, int outlineStartColor, int outlineEndColor, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        Direction blockFacing = blockState.getValue(StairsBlock.FACING);
        StairsBlock.EnumHalf blockHalf = blockState.getValue(StairsBlock.HALF);
        int blockX = blockPos.getX();
        int blockY = blockPos.getY();
        int blockZ = blockPos.getZ();
        int angleX = (blockHalf == StairsBlock.EnumHalf.TOP) ? 270 : 0;
        int angleY = 0;
        switch (blockFacing) {
            case NORTH: angleY = 180; break;
            case EAST: angleY = 90; break;
            case WEST: angleY = 270; break;
            default: break;
        }
        GL11.glPushMatrix();
        GL11.glTranslated(-viewerX, -viewerY, -viewerZ);
        GL11.glTranslated(blockX + 0.5, blockY, blockZ + 0.5);
        GL11.glRotated(angleY, 0.0, 1.0, 0.0);
        GL11.glTranslated(0.0, 0.5, 0.0);
        GL11.glRotated(angleX, 1.0, 0.0, 0.0);
        GL11.glTranslated(-blockX - 0.5, -blockY - 0.5, -blockZ - 0.5);
        if (side == null) {
            drawStairsFull(box, overlayStartColor, overlayEndColor, outlineStartColor, outlineEndColor, overlay, outline, drawer);
        } else {
            drawStairsSide(box, blockHalf, blockFacing, side, overlayStartColor, overlayEndColor, outlineStartColor, outlineEndColor, overlay, outline, drawer);
        }
        GL11.glPopMatrix();
    }

    private static void drawStairsFull(Box box, int overlayStart, int overlayEnd, int outlineStart, int outlineEnd, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        drawStairsTop(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer);
        drawStairsBottom(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer);
        drawStairsNorth(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer);
        drawStairsEast(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer);
        drawStairsSouth(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer);
        drawStairsWest(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer);
    }

    private static void drawStairsSide(Box box, StairsBlock.EnumHalf blockHalf, Direction blockFacing, Direction side, int overlayStart, int overlayEnd, int outlineStart, int outlineEnd, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        Direction mapped = getSide(blockHalf, blockFacing, side);
        switch (mapped) {
            case UP: drawStairsTop(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer); break;
            case DOWN: drawStairsBottom(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer); break;
            case NORTH: drawStairsNorth(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer); break;
            case EAST: drawStairsEast(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer); break;
            case SOUTH: drawStairsSouth(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer); break;
            case WEST: drawStairsWest(box, overlayStart, overlayEnd, outlineStart, outlineEnd, overlay, outline, drawer); break;
        }
    }

    private static void drawStairsTop(Box box, int os, int oe, int ls, int le, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        Box b1 = box.contract(0.0, 0.0, 0.25).offset(0.0, 0.0, 0.25);
        Box b2 = box.contract(0.0, 0.0, 0.25).offset(0.0, -0.5, -0.25);
        drawer.draw(b1, Direction.UP, os, oe, ls, le, overlay, outline);
        drawer.draw(b2, Direction.UP, os, oe, ls, le, overlay, outline);
    }

    private static void drawStairsBottom(Box box, int os, int oe, int ls, int le, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        drawer.draw(box, Direction.DOWN, os, oe, ls, le, overlay, outline);
    }

    private static void drawStairsNorth(Box box, int os, int oe, int ls, int le, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        drawer.draw(box.contract(0.0, 0.252, 0.0).offset(0.0, 0.252, 0.5), Direction.NORTH, os, oe, ls, le, overlay, outline);
        drawer.draw(box.contract(0.0, 0.25, 0.0).offset(0.0, -0.25, 0.0), Direction.NORTH, os, oe, ls, le, overlay, outline);
    }

    private static void drawStairsEast(Box box, int os, int oe, int ls, int le, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        drawer.draw(box.contract(0.0, 0.252, 0.25).offset(0.0, 0.252, 0.25), Direction.EAST, os, oe, ls, le, overlay, outline);
        drawer.draw(box.contract(0.0, 0.25, 0.0).offset(0.0, -0.25, 0.0), Direction.EAST, os, oe, ls, le, overlay, outline);
    }

    private static void drawStairsSouth(Box box, int os, int oe, int ls, int le, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        drawer.draw(box, Direction.SOUTH, os, oe, ls, le, overlay, outline);
    }

    private static void drawStairsWest(Box box, int os, int oe, int ls, int le, boolean overlay, boolean outline, BlockFaceDrawer drawer) {
        drawer.draw(box.contract(0.0, 0.252, 0.25).offset(0.0, 0.252, 0.25), Direction.WEST, os, oe, ls, le, overlay, outline);
        drawer.draw(box.contract(0.0, 0.25, 0.0).offset(0.0, -0.25, 0.0), Direction.WEST, os, oe, ls, le, overlay, outline);
    }

    private static Direction getSide(StairsBlock.EnumHalf blockHalf, Direction blockFacing, Direction side) {
        if (blockHalf == StairsBlock.EnumHalf.TOP) {
            switch (blockFacing) {
                case NORTH:
                    side = side.rotateAround(Direction.Axis.X);
                    side = side.rotateAround(Direction.Axis.Y);
                    side = side.rotateAround(Direction.Axis.Y);
                    break;
                case EAST:
                    side = side.rotateAround(Direction.Axis.Z);
                    side = side.rotateAround(Direction.Axis.Y);
                    break;
                case SOUTH:
                    side = side.rotateAround(Direction.Axis.X);
                    side = side.rotateAround(Direction.Axis.X);
                    side = side.rotateAround(Direction.Axis.X);
                    break;
                case WEST:
                    side = side.rotateAround(Direction.Axis.Z);
                    side = side.rotateAround(Direction.Axis.Y);
                    side = side.rotateAround(Direction.Axis.Z);
                    side = side.rotateAround(Direction.Axis.Z);
                    break;
                default: break;
            }
        } else if (side != Direction.UP && side != Direction.DOWN) {
            switch (blockFacing) {
                case NORTH: side = side.getOpposite(); break;
                case EAST: side = side.rotateYCCW(); break;
                case WEST: side = side.rotateYCCW(); break;
                default: break;
            }
        }
        return side;
    }

    private StairsUtils() {}
}

package the_end_game.particles;

import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;

public class ParticleBlood extends Particle {
	public final float flameSize;
	public static float colorR = 0.0F;
    public static float colorG = 0.0F;
    public static float colorB = 0.0F;

    public ParticleBlood(World world, double posX, double posY, double posZ, double moveX, double moveY, double moveZ, float r, float g, float b, float size) {
        super(world, posX, posY, posZ, moveX, moveY, moveZ);
        colorR = r;
        colorG = g;
        colorB = b;
        if ((double) colorR > 1.0D) { colorR /= 255.0F; }
        if ((double) colorG > 1.0D) { colorG /= 255.0F; }
        if ((double) colorB > 1.0D) { colorB /= 255.0F; }
        setRBGColorF(colorR, colorG, colorB);
        flameSize = particleScale = size;
        particleTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(new ResourceLocation(Main.MODID, "othes/particles/particle_blood").toString());
        
        //particleAlpha = 2.5F;
        motionX = moveX;
        motionY = moveY;
        motionZ = moveZ;
		
    }
	
    public boolean shouldDisableDepth() { return true; }
    public int getFXLayer() { return 1; }
    
    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
    	public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
    		return new ParticleBlood(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 1, colorR, colorG, colorB);
    	}
    }
}

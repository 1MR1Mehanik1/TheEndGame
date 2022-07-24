package the_end_game.items.armor.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class NoArmor extends ModelBiped {
	public NoArmor() {
		textureWidth = 0;
		textureHeight = 0;
		
		bipedHead.cubeList.clear();
        bipedBody.cubeList.clear();
        bipedRightArm.cubeList.clear();
        bipedLeftArm.cubeList.clear();
        bipedRightLeg.cubeList.clear();
        bipedLeftLeg.cubeList.clear();
        
		//bipedHead.addChild(head);
		//bipedBody.addChild(body);
		
		//bipedRightArm.addChild(rightArm);
		//bipedLeftArm.addChild(leftArm);
        
        //bipedRightLeg.addChild(rightLeg);
        //bipedLeftLeg.addChild(leftLeg);
        
        //bipedRightLeg.addChild(rightFoot);
        //bipedLeftLeg.addChild(leftFoot);
	}
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}

package entities;

import player.Animations;
import project.Camera;
import project.Texture;
import project.Window;
import project.World;

public class Rock extends Entity{
	public static final int IDLE = 0;
	public static final int SIZE = 1;
	public Rock(Transform transform) {
		// TODO Auto-generated constructor stub
		super(SIZE,transform);
		setAnimation(IDLE, new Animations(1,1,"worldEntities/rock"));//player standing 
		useAnimation(IDLE);
	
	}
	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		// TODO Auto-generated method stub
	
		
	}
	@Override
	public void destroy() {

	}
//	public void t() {
//		Transform t = new Transform();
//		t.pos.x = 0;
//		t.pos.y = -4;
//	}

}

package player;

import project.Texture;

import project.World;

public class Animations {


	private Texture[] frames;
	private int pointer;//pointer value for array
	
	
	private double elapsedTime;
	private double currentTime;
	private double lastTime;
	private double fps;

	
	
	public Animations(int amount, int fps, String filename) {
	// TODO Auto-generated constructor stub
		this.pointer = 0;
		this.currentTime = 0;
		this.elapsedTime = 0;
		this.lastTime = World.getTime();
		this.fps = 1.0/(double)fps;
		
		this.frames = new Texture[amount];
		for(int i = 0; i < amount ; i++) {
			this.frames[i] = new Texture(filename + "/" + i + ".png");//can find animation in different folders
		}
			
	}
//	public void bind() {
//		bind(0);
//	}
//	
	public void bind(int sampler) {
		this.currentTime = World.getTime();
		this.elapsedTime += currentTime - lastTime;
		
		if(elapsedTime>=fps) {
			elapsedTime = 0;//wont speed up to get where it needs to go
			pointer++;
		}
		
		if(pointer >= frames.length)
			pointer = 0;// keeps array inbounds
		
		this.lastTime = currentTime;
		
		frames[pointer].bind(sampler);
	}
}

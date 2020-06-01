package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class RayTracing extends ApplicationAdapter {
	
	public void generateDirections(int count) { //TODO - MAYBE MAKE THIS MORE EFFICIENT
		double increment = (2 * Math.tan(0.25 * Math.PI)) / count; //Used to use equidistant angles but instead uses equidistant opposites from right angled triangle
		double currentAngle; //TODO - Above tan takes fov / 2
		intersections = new Vec2D[count];
		directions = new Vec2D[count];
		rays = new VectorLine[count];
		distances = new double[count];
		rayColours = new float[count][3];
		
		for (int i = 0; i < count; i++) {
			currentAngle = (Math.atan(increment * (i - (count / 2)))) + angle; //works out angle required in order to increase opposite by fixed amount
			directions[i] = new Vec2D(Math.cos(currentAngle), Math.sin(currentAngle));
		}
		playerDirection = directions[(int)(count - 1)/2];
		sideDirection = new Vec2D(playerDirection.getJ(), -playerDirection.getI());
	}
	
	//public void updateMouse() {
	//	mousePosition = new Vec2D(Gdx.input.getX() * 640 / Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() - Gdx.input.getY()) * 480/ Gdx.graphics.getHeight());
	//}
	
	ShapeRenderer sr;
	static VectorLine[] rays;
	static Vec2D[] directions;
	static Vec2D[] intersections;
	static Vec2D playerPosition = new Vec2D(0, 0);
	static Vec2D playerDirection;
	static Vec2D sideDirection;
	static double[] distances;
	static float[][] rayColours;
	static double angle = 0;
	static double speed = 1;
	static float scale = 0.5f;
	
	static int rayCount = 640;
	static float width = 640 / rayCount;
	
	static float[][][] shapeVertices = new float[][][] {{{400, 20}, {450, 120}, {425, 132.5f}, {375, 32.5f}}, 
														{{20, 20}, {200, 50}, {150, 220}},
														{{300, 300}, {500, 400}, {100, 350}, {200, 295}}};
	
	static float[][] shapeColours = new float[][] {{1, 1, 1}, {1, 0, 0}, {1, 0, 1}};
	
	boolean is3D = true;
	boolean isPressed = false;
	
	@Override
	public void create () {
		sr = new ShapeRenderer();
		//generateDirections(500);
	}
	

	@Override
	public void render () {
		//updateMouse();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) { 
			angle += 0.1;
		}
		
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			angle -= 0.1;
		}
		generateDirections(rayCount);
		
		if (Gdx.input.isKeyPressed(Keys.W)) {
			playerPosition = playerPosition.vecAdd(playerDirection.scalarMultiply(speed));
		}
		
		if (Gdx.input.isKeyPressed(Keys.S)) {
			playerPosition = playerPosition.vecSub(playerDirection.scalarMultiply(speed));
		}
		
		if (Gdx.input.isKeyPressed(Keys.D)) {
			playerPosition = playerPosition.vecAdd(sideDirection.scalarMultiply(speed));
		}
		
		if (Gdx.input.isKeyPressed(Keys.A)) {
			playerPosition = playerPosition.vecSub(sideDirection.scalarMultiply(speed));
		}
		
		if (Gdx.input.isKeyPressed(Keys.Q)) {
			if (isPressed == false) {
				is3D = !is3D;
			}
			isPressed = true;
		}
		else {
			isPressed = false;
		}
		
		
		
		
		
		sr.begin(ShapeType.Line);
		
		for (int i = 0; i < rays.length; i++) {
			rays[i] = new VectorLine(playerPosition, directions[i]);
			
			double minLambda = 100000;
			intersections[i] = playerPosition.vecAdd(directions[i].scalarMultiply(100000));
			
			//sr.setColor(Color.WHITE);
			for (int x = 0; x < shapeVertices.length; x++) {
				for (int z = 0; z < shapeVertices[x].length; z++) {
					int index = (z + 1) % shapeVertices[x].length;
					double changeY = shapeVertices[x][index][1] - shapeVertices[x][z][1];
					double changeX = shapeVertices[x][index][0] - shapeVertices[x][z][0];
					VectorLine side = new VectorLine(new Vec2D(shapeVertices[x][z][0], shapeVertices[x][z][1]), new Vec2D(changeX, changeY));
					sr.setColor(shapeColours[x][0], shapeColours[x][1], shapeColours[x][2], 1);
					sr.line(shapeVertices[x][index][0], shapeVertices[x][index][1], shapeVertices[x][z][0], shapeVertices[x][z][1]);
					
					if (rays[i].findIntersection(side) == true) {
						if (rays[i].getLambda() >= 0) {
							double minX, minY, maxX, maxY;
							if (changeY <= 0) {
								minY = shapeVertices[x][index][1];
								maxY = shapeVertices[x][z][1];
							}
							else {
								minY = shapeVertices[x][z][1];
								maxY = shapeVertices[x][index][1];
							}
							
							if (changeX <= 0) {
								minX = shapeVertices[x][index][0];
								maxX = shapeVertices[x][z][0];
							}
							else {
								minX = shapeVertices[x][z][0];
								maxX = shapeVertices[x][index][0];
							}
							
							double xVal = rays[i].getIntersection().getI(), yVal = rays[i].getIntersection().getJ();
							if (xVal >= minX - 0.001 && xVal <= maxX  + 0.001 && yVal >= minY - 0.001 && yVal <= maxY + 0.001) {
								if (rays[i].getLambda() < minLambda) {
									minLambda = rays[i].getLambda();
									intersections[i] = rays[i].getIntersection();
									rayColours[i] = shapeColours[x].clone();
								}
							}
						}
					}
				}
			}
			distances[i] = minLambda;
		}
		
		
		
		
		
		//sr.setColor(Color.BLACK);
		//for (int i = 0; i < shapeVertices.length; i++) {
		//	sr.line(shapeVertices[i][0][0], shapeVertices[i][0][1], shapeVertices[i][1][0], shapeVertices[i][1][1]);
		//}
		//sr.end();
		
		
		///sr.begin(ShapeType.Line);
		//sr.setColor(Color.WHITE);
		//Vec2D currentPosition, currentDirection;
		//sr.setColor(Color.YELLOW);
		sr.setColor(1, 1, 1, 1);
		for (int i = 0; i < rays.length; i++) {
			//currentPosition = rays[i].getPosition();
			//currentDirection = rays[i].getDirection().scalarMultiply(100000);
			//sr.line((float)currentPosition.getI(), (float)currentPosition.getJ(), (float)(currentPosition.getI() + currentDirection.getI()), (float)(currentPosition.getJ() + currentDirection.getJ()));
			sr.line((float)(rays[i].getPosition().getI()), (float)(rays[i].getPosition().getJ()), (float)(intersections[i].getI()), (float)(intersections[i].getJ()));
		}
		
		//TODO - FOR NOW EVERYTHING IS OVER THE MINIMAP
		sr.end();
		
		
		if (is3D == true) {
			sr.begin(ShapeType.Filled);
			sr.setColor(0, 0, 1, 1);
			sr.rect(0, 0, 640, 480);
			sr.setColor(0, 1, 0, 1);
			sr.rect(0, 0, 640, 240);
			
			
			for (int i = 0; i < distances.length; i++) {
				double multiplier = 0.0000001 + (distances[i] * (directions[i].dotProduct(playerDirection))* 0.01); //distances[i] produces a distorted fish eye distance, therefore this will need to be multiplied by costheta (angle between direction of player and line) to find correct distance
				float height = (float)(240 / multiplier);
				if (height > 240) {
					height = 240;
				}
				//System.out.println(height);
				multiplier = Math.sqrt(multiplier);
				sr.setColor((float)(rayColours[i][0] / multiplier), (float)(rayColours[i][1] / multiplier), (float)(rayColours[i][2] / multiplier), 1);
				sr.rect(640 - (width * (i + 1)), 240 - height, width, 2 * height);
			}
			
			
			sr.end();
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose () {
		sr.dispose();
	}
}

package com.mygdx.game;

public class VectorLine {
	protected Vec2D position, direction, intersection;
	protected double lambda;
	
	public VectorLine(Vec2D position, Vec2D direction) {
		this.position = position;
		this.direction = direction;
	}
	
	public boolean findIntersection(VectorLine otherLine) {
		Matrix2By2 equationMatrix = new Matrix2By2(new double[][] {{this.getDirection().getI(), -otherLine.getDirection().getI()},
																   {this.getDirection().getJ(), -otherLine.getDirection().getJ()}});
		
		Matrix solutionMatrix = new Matrix(new double[][] {{otherLine.getPosition().getI() - this.getPosition().getI()},
														   {otherLine.getPosition().getJ() - this.getPosition().getJ()}});
		
		if (equationMatrix.findInverse() == true) {
			double lambda = new Matrix(equationMatrix.getInverse()).matrixMultiply(solutionMatrix)[0][0];
			this.setLambda(lambda);
			this.setIntersection(this.getPosition().vecAdd(this.getDirection().scalarMultiply(lambda)));
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public Vec2D getPosition() {
		return position;
	}
	
	public Vec2D getDirection() {
		return direction;
	}
	
	public Vec2D getIntersection() {
		return intersection;
	}
	
	public void setIntersection(Vec2D value) {
		intersection = value;
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public void setLambda(double value) {
		lambda = value;
	}
}

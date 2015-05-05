package SkyRacers.core;

import MathClasses.Plane;
import MathClasses.Vector3;
import java.util.ArrayList;

public class FrustumCulling {
    private static final int TOP = 0;
    private static final int BOT = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int NEAR = 4;
    private static final int FAR = 5;
    
    public static final int OUTSIDE = 0;
    public static final int INTERSECT = 1;
    public static final int INSIDE = 2;
    
    public static final float ANG2RAD = (float) (3.1415926536/180.0);
    
    public ArrayList<Plane> planes = new ArrayList<>(6);
    
    public Vector3 nearTopLeft, nearTopRight, nearBottomLeft, nearBottomRight;
    public Vector3 farTopLeft, farTopRight, farBottomLeft, farBottomRight;
    
    public float nearDistance, farDistance, aspect, angle, tang;
    public float nearWidth, nearHeight, farWidth, farHeight;
    
    public FrustumCulling()
    {
        for(int i = 0; i < 6; i++)
        {
            this.planes.add(new Plane());
        }
    }
    
    //Stores info from Camera and computes width and height of the near and far planes
    //Also calculates near's and far's width and height
    public void setCamInternals(float angle, float ratio, float nearDist, float farDist)
    {
        this.aspect = ratio;
        this.angle = angle;
        this.nearDistance = nearDist;
        this.farDistance = farDist;
        
        this.tang = (float)Math.tan(FrustumCulling.ANG2RAD * angle * 0.5);
        this.nearHeight = this.nearDistance * this.tang;
        this.nearWidth = this.nearHeight * this.aspect;
        this.farHeight = this.farDistance * this.tang;
        this.farWidth = this.farHeight * this.aspect;
        
        /*
        System.out.println("aspect: "+this.aspect);
        System.out.println("angle: "+this.angle);
        System.out.println("nearD: "+this.nearDistance);
        System.out.println("farD: "+this.farDistance);
        System.out.println("tang: "+this.tang);
        System.out.println("nearHeight: "+this.nearHeight);
        System.out.println("nearWidth: "+this.nearWidth);
        System.out.println("farHeight: "+this.farHeight);
        System.out.println("farWidth: "+this.farWidth);
        */
    }
    
    //Receives three vectors, the position of the camera, a point where the camera is pointing
    //And the up vector
    public void setCamDef(Vector3 position, Vector3 looking, Vector3 up)
    {
        Vector3 nearCenter, farCenter, xAxis, yAxis, zAxis;
        Vector3 normalX, normalZ;
        //Compute the Z axis of camera, points in the opposite direction from the looking direction
        zAxis = position.sub(looking);
        normalZ = zAxis.normalize();
        
        //X axis of camera with given "up" vector and Z axis
        xAxis = up.cross(normalZ);
        normalX = xAxis.normalize();
        
        //The real "up" vector is the cross product of Z and X
        yAxis = normalZ.cross(normalX);
        
        //Find centers of the near and far planes
        /*
        nc = p - Z * nearD;
	fc = p - Z * farD;
        */
        nearCenter = position.sub(normalZ.mul(this.nearDistance));
        farCenter = position.sub(normalZ.mul(this.farDistance));
        
        // compute the 4 corners of the frustum on the near plane
	/*
        ntl = nc + Y * nh - X * nw;
	ntr = nc + Y * nh + X * nw;
	nbl = nc - Y * nh - X * nw;
	nbr = nc - Y * nh + X * nw;
        */
        this.nearTopLeft = nearCenter.add(yAxis.mul(this.nearHeight).sub(normalX.mul(this.nearWidth)));
        this.nearTopRight = nearCenter.add(yAxis.mul(this.nearHeight).add(normalX.mul(this.nearWidth)));
        this.nearBottomLeft = nearCenter.sub(yAxis.mul(this.nearHeight).sub(normalX.mul(this.nearWidth)));
        this.nearBottomRight = nearCenter.sub(yAxis.mul(this.nearHeight).add(normalX.mul(this.nearWidth)));
        
        // compute the 4 corners of the frustum on the far plane
	/*
        ftl = fc + Y * fh - X * fw;
	ftr = fc + Y * fh + X * fw;
	fbl = fc - Y * fh - X * fw;
	fbr = fc - Y * fh + X * fw;
        */
        this.farTopLeft = farCenter.add(yAxis.mul(this.farHeight).sub(normalX.mul(this.farWidth)));
        this.farTopRight = farCenter.add(yAxis.mul(this.farHeight).add(normalX.mul(this.farWidth)));
        this.farBottomLeft = farCenter.sub(yAxis.mul(this.farHeight).sub(normalX.mul(this.farWidth)));
        this.farBottomRight = farCenter.sub(yAxis.mul(this.farHeight).add(normalX.mul(this.farWidth)));
        
        // compute the six planes
	// the function set3Points assumes that the points
	// are given in counter clockwise order
	this.planes.get(FrustumCulling.TOP).createPlane3Points(this.nearTopRight, this.nearTopLeft, this.farTopLeft);
        this.planes.get(FrustumCulling.BOT).createPlane3Points(this.nearBottomLeft, this.nearBottomRight, this.farBottomRight);
        this.planes.get(FrustumCulling.LEFT).createPlane3Points(this.nearTopLeft, this.nearBottomLeft, this.farBottomLeft);
        this.planes.get(FrustumCulling.RIGHT).createPlane3Points(this.nearBottomRight, this.nearTopRight, this.farBottomRight);
        this.planes.get(FrustumCulling.NEAR).createPlane3Points(this.nearTopLeft, this.nearTopRight, this.nearBottomRight);
        this.planes.get(FrustumCulling.FAR).createPlane3Points(this.farTopRight, this.farTopLeft, this.farBottomLeft);
        
        /*
        pl[TOP].set3Points(ntr,ntl,ftl);
	pl[BOTTOM].set3Points(nbl,nbr,fbr);
	pl[LEFT].set3Points(ntl,nbl,fbl);
	pl[RIGHT].set3Points(nbr,ntr,fbr);
	pl[NEARP].set3Points(ntl,ntr,nbr);
	pl[FARP].set3Points(ftr,ftl,fbl);
        */
    }
    
    public int pointInFrustum(Vector3 point)
    {
        int result = FrustumCulling.INSIDE;
        for(int i = 0; i < 6; i++)
        {
            System.out.println("i: "+i+" dist: "+ this.planes.get(i).distance(point));
            if(this.planes.get(i).distance(point) < 0)
                return FrustumCulling.OUTSIDE;
        }
        return result;
    }
    
    public int sphereInFrustum(Vector3 point, float radius)
    {
        float distance;
        int result = FrustumCulling.INSIDE;
        for(int  i = 0; i < 6; i++)
        {
            distance = this.planes.get(i).distance(point);
            if(distance < -radius)
                return FrustumCulling.OUTSIDE;
            else if (distance < radius)
                result = FrustumCulling.INTERSECT;
        }
        return result;
    }
    
}

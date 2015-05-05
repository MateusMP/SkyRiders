package MathClasses;

public class Plane 
{
    private Vector3 point;
    private float d;
    private Vector3 normal;
    
    //Create a plane using 3 points
    public void createPlane3Points(Vector3 point1, Vector3 point2, Vector3 point3)
    {
        Vector3 aux1 = point2.sub(point1);
        Vector3 aux2 = point3.sub(point2);
        Vector3 normal = aux2.cross(aux1);
        this.normal = normal.normalize();
        this.point = new Vector3(point2.x, point2.y, point2.z);
        this.d = -(this.normal.dot(point2));
        //createPlaneNormalAndPoint(point1, normalized);
    }
    
    //Create a plane using normal vector and a point
    public void createPlaneNormalAndPoint(Vector3 normalVector, Vector3 point)
    {
        Vector3 normalized = normalVector.clone();
        this.normal = (normalized.normalize()).clone();
        this.d = -(this.normal.dot(this.point));
    }
    
    //Returns the signed distance from point to vector
    public float distance(Vector3 point)
    {
        //return (this.normal.dot(point.sub(new Vector3(this.a, this.b, this.c))));
        //return (this.d + this.normal.dot(point));
        return (this.point.x * point.x + this.point.y + point.y + this.point.z * point.z + this.d);
        //return (this.a * point.x + this.b * point.y + this.c * point.z + this.d);
    }
    
}

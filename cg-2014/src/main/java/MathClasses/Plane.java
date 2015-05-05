package MathClasses;

public class Plane 
{
    private float a;
    private float b;
    private float c;
    private float d;
    
    //Create a plane using 3 points
    public void createPlane3Points(Vector3 point1, Vector3 point2, Vector3 point3)
    {
        Vector3 aux1 = point2.sub(point1);
        Vector3 aux2 = point3.sub(point1);
        Vector3 normal = aux1.cross(aux2);
        normal = normal.normalize();
        createPlaneNormalAndPoint(point1, normal);
    }
    
    //Create a plane using normal vector and a point
    public void createPlaneNormalAndPoint(Vector3 normalVector, Vector3 point)
    {
        Vector3 normalizedNormal = normalVector.normalize();
        this.a = normalizedNormal.x;
        this.b = normalizedNormal.y;
        this.c = normalizedNormal.z;
        this.d = point.dot(normalizedNormal);
    }
    
    //Returns the signed distance from point to vector
    public float distance(Vector3 point)
    {
        return (this.a * point.x + this.b * point.y + this.c * point.z + this.d);
    }
}

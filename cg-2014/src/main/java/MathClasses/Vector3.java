package MathClasses;

public class Vector3
{
    public float x;
    public float y;
    public float z;
    
    public Vector3()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    public Vector3(Vector3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(float[] array)
    {        
        if(array.length != 3)
                throw new RuntimeException("Must create vector with 3 element array");

        this.x = array[0];
        this.y = array[1];
        this.z = array[2];
    }

    public Vector3 clone()
    {
        return new Vector3(x, y, z);
    }
    
    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 rhs)
    {
        return new Vector3(
                this.x + rhs.x,
                this.y + rhs.y,
                this.z + rhs.z );
    }

    public Vector3 sub(Vector3 rhs)
    {
        return new Vector3(
                this.x - rhs.x,
                this.y - rhs.y,
                this.z - rhs.z );
    }

    public Vector3 neg()
    {
        return new Vector3(-this.x, -this.y, -this.z);
    }

    public Vector3 mul(float c)
    {
        return new Vector3(c*this.x, c*this.y, c*this.z);
    }

    public Vector3 div(float c)
    {
        return new Vector3(this.x/c, this.y/c, this.z/c);
    }

    public float dot(Vector3 rhs)
    {
        return this.x*rhs.x +
                this.y*rhs.y +
                this.z*rhs.z;
    }

    public Vector3 cross(Vector3 rhs)
    {
        return new Vector3(
                this.y*rhs.z - this.z*rhs.y,
                this.x*rhs.z - this.z*rhs.x,
                this.x*rhs.y - this.y*rhs.x
        );
    }
    
    public Vector3 scale(Vector3 scales){
        return new Vector3( x*scales.x, y*scales.y, z*scales.z );
    }

    public boolean equals(Object obj)
    {
        if( obj instanceof Vector3 )
        {
                Vector3 rhs = (Vector3)obj;

                return this.x==rhs.x &&
                       this.y==rhs.y &&
                       this.z==rhs.z;
        }
        else
        {
                return false;
        }
    }

    public float norm()
    {
        return (float) Math.sqrt(this.dot(this));	
    }
    
    public float norm2()
    {
        return this.dot(this);	
    }

    public Vector3 normalize()
    {
        return this.div(norm());
    }

    public String toString()
    {
        return "( " + this.x + " " + this.y + " " + this.z + " )"; 
    }
    
    public float calcAbsolDistance(Vector3 point)
    {
        return (float)Math.sqrt(Math.pow((this.x - point.x),2) + Math.pow((this.y - point.y),2) + Math.pow((this.z - point.z),2));
    }
    
    
    public Vector3 orthogonal() {

        double threshold = 0.6 * norm();
        if (threshold == 0) {
            return null;
        }

        if (Math.abs(x) <= threshold) {
            double inverse  = 1 / Math.sqrt(y * y + z * z);
            return new Vector3(0, (float)inverse * z, (float)-inverse * y);
        } else if (Math.abs(y) <= threshold) {
            double inverse  = 1 / Math.sqrt(x * x + z * z);
            return new Vector3((float)-inverse * z, 0, (float)inverse * x);
        }
        double inverse  = 1 / Math.sqrt(x * x + y * y);
        return new Vector3((float)inverse * y, (float)-inverse * x, 0);
    }

}
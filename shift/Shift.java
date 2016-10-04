package shift;

public class Shift 
{
    //String[] args = {"-Dsun.java2d.accthreshold=0", -Dsun.java2d.opengl=true};
    public static void main(String[] args)
    {
        for(String s : args)
        {
            System.out.println(s);
        }
        Frame f=new Frame();
    }
}

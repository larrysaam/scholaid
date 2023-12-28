package school;


public class Teacher {
    private String Teachername;
    private String matricule;

    public Teacher(String matricule, String Teachername){
        this.Teachername = Teachername;
        this.matricule = matricule;
    }


    public void setMatricule(String mat){
        this.matricule = mat;
    }

    public String getMatricule(){
        return matricule;
    }

    public void setName(String name){
        this.Teachername = name;
    }

    public String getName(){
        return Teachername;
    }
}

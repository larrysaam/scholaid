package school;

public class Student {

    private String matricule;
    private String name;


    public Student(String mat, String name){
        this.matricule = mat;
        this.name = name;
    }

    public void setMatricule(String mat){
        this.matricule = mat;
    }

    public String getMatricule(){
        return matricule;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    
}

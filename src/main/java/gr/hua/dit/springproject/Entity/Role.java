package gr.hua.dit.springproject.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Role_id")
    private int role_id;

    @Enumerated(EnumType.STRING)
    @Column(name="Name")
    private EnumRole name;



    public Role() {}

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public EnumRole getName() {
        return name;
    }

    public void setName(EnumRole name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" + ",\n" +
                "role_id=" + role_id + ",\n" +
                "name=" + name + "',\n" +
                '}';
    }
}

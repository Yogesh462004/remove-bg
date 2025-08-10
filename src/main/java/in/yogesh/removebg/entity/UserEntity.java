package in.yogesh.removebg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String clerkid;
    @Column(unique = true,nullable = false)
    private String email;
    private String firstName;
    private String lastName;
    private Integer credits;
    private String photoUrl;

    @PrePersist
    public void prepersist(){
        if(credits==null){
            credits=5;
        }
    }
}

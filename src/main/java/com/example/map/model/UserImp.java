package com.example.map.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
        }
)
@NoArgsConstructor
@NamedEntityGraph(
        name = "graph.user",
        attributeNodes = {
                @NamedAttributeNode("username"),
                @NamedAttributeNode("email"),
                @NamedAttributeNode(value = "roles", subgraph = "graph.role")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "graph.role",
                        attributeNodes = {
                                @NamedAttributeNode("name")
                        }
                )
        }
)
public class UserImp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "user_id")
            },
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "userImp", cascade = CascadeType.ALL)
    private List<Report> reports;

    public UserImp(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

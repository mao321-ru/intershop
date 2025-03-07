package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table( name = "orders")
@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Order {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "order_id")
    private Long id;

    // заполняется из последовательности БД
    @Column( name = "order_number", nullable = false, updatable = false, insertable = false)
    private Long number;

    @Column( name = "order_total", nullable = false)
    private BigDecimal total;

    @OneToMany( mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderProduct> products;

    @Override
    public boolean equals( Object o) {
        if( this == o) return true;
        if( o == null || getClass() != o.getClass()) return false;
        Order other = (Order) o;
        return id != null && Objects.equals( id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash( id);
    }
}

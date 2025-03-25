package org.example.intershop.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

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
    @Column( "order_id")
    private Long id;

    // заполняется из последовательности БД
    @Column( "order_number")
    private Long number;

    @Column( "order_total")
    private BigDecimal total;

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

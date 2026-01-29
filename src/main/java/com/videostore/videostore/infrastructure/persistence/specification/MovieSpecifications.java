package com.videostore.videostore.infrastructure.persistence.specification;

import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.RentalEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecifications {

    public static Specification<MovieEntity> genreEquals(String genre) {
        return (root, query, criteriaBuilder) ->
                genre == null || genre.isBlank() ? null : criteriaBuilder.equal(root.get("genre"), genre);
    }

    public static Specification<MovieEntity> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                title == null || title.isBlank() ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<MovieEntity> onlyAvailable(boolean onlyAvailable) {
        return (root, query, cb) -> {
            if (!onlyAvailable) {
                return null;
            }

            assert query != null;

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<RentalEntity> rentalRoot = subquery.from(RentalEntity.class);

            subquery.select(cb.count(rentalRoot))
                    .where(
                            cb.equal(rentalRoot.get("movie"), root)
                    );

            return cb.greaterThan(
                    root.get("numberOfCopies"),
                    subquery
            );
        };
    }
}

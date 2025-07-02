package com.booking.bookingbackend.data.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.Review;

@Repository
public interface ReviewRepository extends BaseRepository<Review, Integer> {

    @Query(
            value =
                    """
			select reviews.id  AS id,
					rating       AS rating,
					review      AS review,
					avatar      AS avatar,
					first_name  AS name,
					nationality AS nationality,
					created_at  AS createdAt
			from (select id,
						review,
						rating,
						user_id,
						created_at
					from tbl_reviews tr
					where properties_id = :propertiesId) reviews
					join tbl_profile on tbl_profile.user_id = reviews.user_id
			order by created_at desc
			""",
            countQuery =
                    """
		select count(*)
		from (select id,
					review,
					rating,
					user_id,
					created_at
				from tbl_reviews tr
				where properties_id = :propertiesId) reviews
				join tbl_profile on tbl_profile.user_id = reviews.user_id
		""",
            nativeQuery = true)
    Page<ReviewResponse> findAllByPropertiesId(@Param("propertiesId") UUID propertiesId, Pageable pageable);
}

package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime reviewDate = LocalDateTime.now();

    private Boolean approved = true;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus status = FeedbackStatus.NEW;

    public Review() {}

    public Review(Long id, Customer user, Trip trip, Integer rating, String comment, LocalDateTime reviewDate, Boolean approved) {
        this.id = id;
        this.user = user;
        this.trip = trip;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.approved = approved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getUser() { return user; }
    public void setUser(Customer user) { this.user = user; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }

    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }
}
package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "guide_languages")
@IdClass(GuideLanguage.Key.class)
public class GuideLanguage {

    @Id
    @Column(name = "guide_id", nullable = false)
    private Long guideId;

    @Id
    @Column(name = "language", nullable = false, length = 30)
    private String language;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Guide guide;

    public GuideLanguage() {}

    public Long getGuideId() { return guideId; }
    public void setGuideId(Long guideId) { this.guideId = guideId; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Guide getGuide() { return guide; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long guideId;
        private String language;
        public Key() {}
        public Key(Long guideId, String language) {
            this.guideId = guideId;
            this.language = language;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(guideId, k.guideId) && Objects.equals(language, k.language);
        }
        @Override public int hashCode() { return Objects.hash(guideId, language); }
    }
}

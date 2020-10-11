package com.pplenty.domain;

import com.pplenty.dto.SharingResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PROTECTED;

/**
 * Created by yusik on 2020/10/09.
 */
@ToString
@Getter
@Entity
@NoArgsConstructor(access = PROTECTED) // proxy 생성 때문에 jpa 에서는 기본 생성자가 있어야함
public class Sharing {

    private static final int EXPIRY_PERIOD = 10 * 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    private int totalAmount;

    private long userId;

    private long roomId;

    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "sharing", cascade = ALL)
    private List<Distribution> distributions = new ArrayList<>();

    @Builder
    public Sharing(String token, int totalAmount, long userId, long roomId, List<Distribution> distributions, LocalDateTime createdDate) {
        this.token = token;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.roomId = roomId;
        addAll(distributions);
        this.createdDate = createdDate;
    }

    public List<Distribution> getSharedDistributions() {
        return getDistributions().stream()
                .filter(Distribution::isDone)
                .collect(Collectors.toList());
    }

    public boolean isSameUser(long userId) {
        return this.userId == userId;
    }

    public boolean isDifferentRoom(long roomId) {
        return this.roomId != roomId;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(createdDate.plusSeconds(EXPIRY_PERIOD));
    }

    private void addAll(List<Distribution> distributions) {
        for (Distribution distribution : distributions) {
            distribution.setSharing(this);
            this.distributions.add(distribution);
        }
    }
}

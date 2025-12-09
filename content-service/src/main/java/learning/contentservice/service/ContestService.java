package learning.contentservice.service;

import learning.contentservice.dto.ContestDTO;
import learning.contentservice.dto.ProblemDTO;
import learning.contentservice.entity.Contest;
import learning.contentservice.entity.Problem;
import learning.contentservice.repository.ContestRepository;
import learning.contentservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestService {
    private final ContestRepository contestRepository;
    private final ProblemRepository problemRepository;
    private final ProblemService problemService;
    private final MongoTemplate mongoTemplate;

    /**
     * Lấy danh sách cuộc thi (Public/User View)
     */
    public Page<ContestDTO> getContests(String status, String difficulty, Pageable pageable){
        Query query = new Query();
        Instant now = Instant.now();

        if (status != null) {
            switch (status.toUpperCase()) {
                case "UPCOMING":
                    query.addCriteria(Criteria.where("startTime").gt(now));
                    break;
                case "RUNNING":
                    query.addCriteria(Criteria.where("startTime").lte(now).and("endTime").gte(now));
                    break;
                case "ENDED":
                    query.addCriteria(Criteria.where("endTime").lt(now));
                    break;
                case "ACTIVE": // Đang chạy hoặc sắp chạy
                    query.addCriteria(Criteria.where("endTime").gt(now));
                    break;
            }
        }

        if (difficulty != null && !difficulty.isEmpty()) {
            query.addCriteria(Criteria.where("difficulty").is(difficulty));
        }

        long total = mongoTemplate.count(query, Contest.class);

        if (!pageable.getSort().isSorted()) {
            query.with(pageable.getSort().and(Sort.by(Sort.Direction.DESC, "startTime")));
        } else {
            query.with(pageable);
        }

        List<Contest> contests = mongoTemplate.find(query, Contest.class);
        List<ContestDTO> dtos = contests.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    /**
     * Tạo Contest tự động
     */
    public Contest createAutoContest(String title, int durationMinutes, int problemCount, String difficulty){
        List<Problem> randomProblems = problemRepository.getRandomProblems(problemCount, difficulty);

        if(randomProblems.size() < problemCount){
            throw new RuntimeException(String.format(
                    "Kho bài tập không đủ. Yêu cầu: %d, Tìm thấy: %d (Độ khó: %s)",
                    problemCount, randomProblems.size(), difficulty != null ? difficulty : "ALL"
            ));
        }

        List<String> problemIds = randomProblems.stream()
                .map(Problem::getId)
                .collect(Collectors.toList());

        Contest contest = new Contest();
        contest.setTitle(title);
        contest.setProblemIds(problemIds);

        Instant now = Instant.now();
        contest.setStartTime(now);
        contest.setEndTime(now.plus(durationMinutes, ChronoUnit.MINUTES));

        return contestRepository.save(contest);
    }

    public ContestDTO getContestDetail(String id){
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        ContestDTO dto = mapToDTO(contest); // Tái sử dụng logic map cơ bản

        Instant now = Instant.now();
        if (now.isBefore(contest.getStartTime())) {
            dto.setProblems(new ArrayList<>());
            dto.setStatus("UPCOMING");
        } else {
            dto.setProblems(fetchProblems(contest.getProblemIds()));

            if (now.isAfter(contest.getEndTime())) {
                dto.setStatus("ENDED");
            } else {
                dto.setStatus("RUNNING");
            }
        }

        return dto;
    }

    public Contest updateContest(String id, Contest req) {
        Contest ct = contestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        if (req.getTitle() != null) ct.setTitle(req.getTitle());
        if (req.getDescription() != null) ct.setDescription(req.getDescription());
        // if (req.getDifficulty() != null) ct.setDifficulty(req.getDifficulty());

        if (req.getStartTime() != null) ct.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) ct.setEndTime(req.getEndTime());

        if (ct.getStartTime().isAfter(ct.getEndTime())) {
            throw new RuntimeException("StartTime must be before EndTime");
        }

        if (req.getProblemIds() != null) ct.setProblemIds(req.getProblemIds());

        ct.setUpdatedAt(Instant.now());
        return contestRepository.save(ct);
    }

    public void deleteContest(String id) {
        if (!contestRepository.existsById(id)) {
            throw new RuntimeException("Contest not found");
        }
        contestRepository.deleteById(id);
    }


    private List<ProblemDTO> fetchProblems(List<String> ids){
        return ids.stream()
                .map(problemService::getProblemForUser)
                .collect(Collectors.toList());
    }

    private ContestDTO mapToDTO(Contest c) {
        ContestDTO dto = new ContestDTO();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setStartTime(c.getStartTime());
        dto.setEndTime(c.getEndTime());

        long duration = java.time.Duration.between(c.getStartTime(), c.getEndTime()).toMinutes();
        dto.setDurationMinutes(duration);

        Instant now = Instant.now();
        if (now.isBefore(c.getStartTime())) dto.setStatus("UPCOMING");
        else if (now.isAfter(c.getEndTime())) dto.setStatus("ENDED");
        else dto.setStatus("RUNNING");

        return dto;
    }
}
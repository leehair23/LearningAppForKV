package learning.contentservice.service;

import learning.contentservice.dto.ContestDTO;
import learning.contentservice.dto.ProblemDTO;
import learning.contentservice.entity.Contest;
import learning.contentservice.entity.Problem;
import learning.contentservice.repository.ContestRepository;
import learning.contentservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
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
    /**
     * Tạo Contest ngẫu nhiên (Auto Generator)
     * @param title Tên cuộc thi
     * @param durationMinutes Thời lượng (phút)
     * @param problemCount Số lượng bài
     * @param difficulty Độ khó mong muốn (EASY/MEDIUM/HARD hoặc null để random tất cả)
     */
    public Contest createAutoContest(String title, int durationMinutes, int problemCount, String difficulty){
        List<Problem> randomProblems = problemRepository.getRandomProblems(problemCount, difficulty);
        if(randomProblems.size() < problemCount){
            throw new RuntimeException("Problem list doesnt meet required");
        }
        List<String> problemIds = randomProblems.stream()
                .map(Problem::getId)
                .toList();

        Contest contest = new  Contest();
        contest.setTitle(title);
        contest.setProblemIds(problemIds);

        Instant now = Instant.now();
        contest.setStartTime(now);
        contest.setEndTime(now.plus(durationMinutes, ChronoUnit.MINUTES));

        return contestRepository.save(contest);
    }
    public ContestDTO getContestDetail(String id){
        Contest contest = contestRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Contest not found"));
        ContestDTO dto = new ContestDTO();
        dto.setId(contest.getId());
        dto.setTitle(contest.getTitle());
        dto.setStartTime(contest.getStartTime());
        dto.setEndTime(contest.getEndTime());
        if (Instant.now().isBefore(contest.getStartTime())) {
            dto.setProblems(new ArrayList<>());
            dto.setStatus("UPCOMING");
        } else {
            List<ProblemDTO> problems = contest.getProblemIds().stream()
                    .map(problemService::getProblemForUser)
                    .collect(Collectors.toList());
            dto.setProblems(problems);

            if (Instant.now().isAfter(contest.getEndTime())) dto.setStatus("ENDED");
            else dto.setStatus("RUNNING");
        }
        return dto;
    }
}

package learning.contentservice.service;

import learning.contentservice.dto.ProblemDTO;
import learning.contentservice.dto.TestCaseDTO;
import learning.contentservice.entity.Problem;
import learning.contentservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    //public view
    public Page<Problem> getProblems(String keyword, String difficulty, Pageable pageable) {
        if(keyword != null && !keyword.isEmpty()) {
            if (difficulty != null) {
                return problemRepository.findByTitleContainingIgnoreCaseAndDifficulty(keyword, difficulty, pageable);
            }
            return problemRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        }
        return problemRepository.findAll(pageable);
    }
    public ProblemDTO getProblemForUser(String id){
        Problem p = problemRepository.findById(id).orElseThrow(()-> new RuntimeException("Not found"));

        ProblemDTO dto = new ProblemDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setDifficulty(p.getDifficulty());
        dto.setTags(p.getTags());
        dto.setTimeLimit(p.getTimeLimit());
        dto.setMemoryLimit(p.getMemoryLimit());
        dto.setTemplates(p.getTemplates());

        List<TestCaseDTO> samples = p.getTestCases().stream()
                .filter(t -> !t.isHidden())
                .map(t -> {
                    TestCaseDTO tDto = new TestCaseDTO();
                    tDto.setInput(t.getInput());
                    tDto.setExpectedOutput(t.getExpectedOutput());
                    return tDto;
                })
                .collect(Collectors.toList());
        dto.setSampleTestcases(samples);
        return dto;
    }
    //admin stuff (CRUD)
    public Problem createProblem(Problem problem){
        if(problem.getTimeLimit() == null) problem.setTimeLimit(1.0);
        if (problem.getMemoryLimit() == null) problem.setMemoryLimit(256000L);
        problem.setCreatedAt(Instant.now());
        problem.setUpdatedAt(Instant.now());
        return problemRepository.save(problem);
    }
    public Problem updateProblem(String id, Problem req){
        Problem p = problemRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Not found"));

        if (req.getTitle() != null) p.setTitle(req.getTitle());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getDifficulty() != null) p.setDifficulty(req.getDifficulty());
        if (req.getTags() != null) p.setTags(req.getTags());

        if (req.getTimeLimit() != null) p.setTimeLimit(req.getTimeLimit());
        if (req.getMemoryLimit() != null) p.setMemoryLimit(req.getMemoryLimit());

        if (req.getTemplates() != null) p.setTemplates(req.getTemplates());
        if (req.getDriverCode() != null) p.setDriverCode(req.getDriverCode());
        if (req.getTestCases() != null) p.setTestCases(req.getTestCases());

        p.setUpdatedAt(Instant.now());
        return problemRepository.save(p);
    }
    public void deleteProblem(String id){
        if(!problemRepository.existsById(id)){
            throw new RuntimeException("Not found");
        }
        problemRepository.deleteById(id);
    }
    //internal (submission-service)
    public Problem getProblemFull(String id){
        return problemRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }
}

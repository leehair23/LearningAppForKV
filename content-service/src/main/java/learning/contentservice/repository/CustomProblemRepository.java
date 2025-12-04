package learning.contentservice.repository;

import learning.contentservice.entity.Problem;

import java.util.List;

public interface CustomProblemRepository {
    List<Problem> getRandomProblems(int count, String difficulty);
}

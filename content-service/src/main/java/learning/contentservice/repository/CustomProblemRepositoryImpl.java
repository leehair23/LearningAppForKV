package learning.contentservice.repository;

import learning.contentservice.entity.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CustomProblemRepositoryImpl implements CustomProblemRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Problem> getRandomProblems(int count, String difficulty) {
        List<AggregationOperation> stages = new ArrayList<>();
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            MatchOperation matchStage = Aggregation.match(Criteria.where("difficulty").regex("^" + difficulty + "$", "i"));
            stages.add(matchStage);
        }
        SampleOperation sampleStage = Aggregation.sample(count);
        stages.add(sampleStage);

        Aggregation aggregation = Aggregation.newAggregation(stages);
        log.info("Aggregation : {}", aggregation.toString());
        return mongoTemplate.aggregate(aggregation, Problem.class, Problem.class).getMappedResults();
    }
}

package learning.contentservice.repository;

import learning.contentservice.entity.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

@RequiredArgsConstructor
public class CustomProblemRepositoryImpl implements CustomProblemRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Problem> getRandomProblems(int count, String difficulty) {
        MatchOperation matchStage = null;
        if(difficulty != null && !difficulty.isEmpty()){
            matchStage = Aggregation.match(Criteria.where("difficulty").is(difficulty));
        }
        SampleOperation sampleStage = Aggregation.sample(count);

        Aggregation aggregation;
        if(matchStage != null){
            aggregation = Aggregation.newAggregation(matchStage, sampleStage);
        }else{
            aggregation = Aggregation.newAggregation(sampleStage);
        }
        return mongoTemplate.aggregate(aggregation, "problem", Problem.class).getMappedResults();
    }
}

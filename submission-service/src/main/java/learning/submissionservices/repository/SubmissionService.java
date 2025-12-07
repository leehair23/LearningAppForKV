package learning.submissionservices.repository;

import learning.submissionservices.dto.SubmissionResponse;
import learning.submissionservices.dto.SubmissionbRequest;
import learning.submissionservices.dto.runtime.CodeExecuteRequest;
import learning.submissionservices.dto.runtime.CodeExecuteResponse;

public interface SubmissionService {
    SubmissionResponse create(String id, SubmissionbRequest req);
    SubmissionResponse getById(String id);
    void processCallback(String id, CodeExecuteResponse response);
}

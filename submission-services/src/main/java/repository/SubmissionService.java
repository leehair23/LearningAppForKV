package repository;

import dto.SubmissionDTO;

public interface SubmissionService {
    SubmissionDTO.Response create(SubmissionDTO.CreateRequest request);
    SubmissionDTO.Response getById(String id);
    void processCallback(String id, SubmissionDTO.WorkerCallBack callbackData);
}

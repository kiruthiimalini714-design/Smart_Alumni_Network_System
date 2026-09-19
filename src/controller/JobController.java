package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Job;
import service.JobService;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JobController implements HttpHandler {

    private final JobService jobService = new JobService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpHelper.sendJsonResponse(exchange, 200, Map.of("status", "ok"));
            return;
        }

        try {
            int userId = HttpHelper.getUserIdHeader(exchange);
            String role = HttpHelper.getUserRoleHeader(exchange);

            String[] parts = path.split("/");
            Integer jobId = null;
            if (parts.length >= 4 && parts[3].matches("\\d+")) {
                jobId = Integer.parseInt(parts[3]);
            }

            if ("GET".equalsIgnoreCase(method)) {
                if (jobId != null) {
                    Job j = jobService.getJobById(jobId);
                    if (j != null) {
                        HttpHelper.sendJsonResponse(exchange, 200, j);
                    } else {
                        HttpHelper.sendErrorResponse(exchange, 404, "Job posting not found.");
                    }
                } else {
                    Map<String, String> query = HttpHelper.parseQueryParams(exchange);
                    String search = query.get("search");
                    String location = query.get("location");
                    String myJobs = query.get("my");
                    if ("true".equalsIgnoreCase(myJobs) && userId > 0) {
                        List<Job> list = jobService.getJobsByAlumni(userId);
                        HttpHelper.sendJsonResponse(exchange, 200, list);
                    } else {
                        List<Job> list = jobService.getAllJobs(search, location);
                        HttpHelper.sendJsonResponse(exchange, 200, list);
                    }
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                if (userId <= 0 || (!"Alumni".equalsIgnoreCase(role) && !"Admin".equalsIgnoreCase(role))) {
                    HttpHelper.sendErrorResponse(exchange, 403, "Only verified alumni can post jobs.");
                    return;
                }
                Map<String, Object> body = HttpHelper.readJsonBody(exchange);
                String title = JsonUtil.getString(body, "jobTitle", "");
                String company = JsonUtil.getString(body, "company", "");
                String location = JsonUtil.getString(body, "location", "");
                String jobType = JsonUtil.getString(body, "jobType", "Full-Time");
                String salary = JsonUtil.getString(body, "salaryRange", "");
                String desc = JsonUtil.getString(body, "description", "");
                String skills = JsonUtil.getString(body, "requiredSkills", "");
                String link = JsonUtil.getString(body, "applicationLink", "");

                Map<String, Object> res = jobService.createJob(userId, title, company, location, jobType, salary, desc, skills, link);
                boolean success = Boolean.TRUE.equals(res.get("success"));
                HttpHelper.sendJsonResponse(exchange, success ? 201 : 400, res);

            } else if ("PUT".equalsIgnoreCase(method) && jobId != null) {
                if (userId <= 0) {
                    HttpHelper.sendErrorResponse(exchange, 401, "Authentication required.");
                    return;
                }
                Map<String, Object> body = HttpHelper.readJsonBody(exchange);
                String title = JsonUtil.getString(body, "jobTitle", "");
                String company = JsonUtil.getString(body, "company", "");
                String location = JsonUtil.getString(body, "location", "");
                String jobType = JsonUtil.getString(body, "jobType", "Full-Time");
                String salary = JsonUtil.getString(body, "salaryRange", "");
                String desc = JsonUtil.getString(body, "description", "");
                String skills = JsonUtil.getString(body, "requiredSkills", "");
                String link = JsonUtil.getString(body, "applicationLink", "");

                Map<String, Object> res = jobService.updateJob(userId, jobId, title, company, location, jobType, salary, desc, skills, link);
                boolean success = Boolean.TRUE.equals(res.get("success"));
                HttpHelper.sendJsonResponse(exchange, success ? 200 : 400, res);

            } else if ("DELETE".equalsIgnoreCase(method) && jobId != null) {
                if (userId <= 0) {
                    HttpHelper.sendErrorResponse(exchange, 401, "Authentication required.");
                    return;
                }
                Map<String, Object> res = jobService.deleteJob(userId, role, jobId);
                boolean success = Boolean.TRUE.equals(res.get("success"));
                HttpHelper.sendJsonResponse(exchange, success ? 200 : 403, res);

            } else {
                HttpHelper.sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            HttpHelper.sendErrorResponse(exchange, 500, "Server Error: " + e.getMessage());
        }
    }
}

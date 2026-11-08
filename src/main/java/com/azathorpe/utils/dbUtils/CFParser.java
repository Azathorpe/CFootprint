package com.azathorpe.utils.dbUtils;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.Problem;
import com.azathorpe.entities.Problems;
import com.azathorpe.entities.Submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CFParser {
    protected static HashMap<Integer, List<Problem>> parseProblem(String input) {
        Problems problems = JSON.parseObject(input, Problems.class);
        HashMap<Integer, List<Problem>> contestMap = new HashMap<>();
        for (Problem p : problems.getProblems()) {
            if (contestMap.containsKey(p.getContestId())) {
                contestMap.get(p.getContestId()).add(p);
            } else {
                List<Problem> problemList = new ArrayList<>();
                problemList.add(p);
                contestMap.put(p.getContestId(), problemList);
            }
        }
        return contestMap;
    }

    protected static HashMap<Problem, Submission.Verdict> parseSubmission(String input) {
        List<Submission> submissions = JSON.parseArray(input, Submission.class);
        HashMap<Problem, Submission.Verdict> passMap = new HashMap<>(){};
        for (
                Submission submission : submissions) {
            if (passMap.containsKey(submission.getProblem())) {
                if (submission.getVerdict() == Submission.Verdict.OK) {
                    passMap.put(submission.getProblem(), submission.getVerdict());
                }
            } else {
                passMap.put(submission.getProblem(), submission.getVerdict());
            }
        }

        return passMap;
    }
}

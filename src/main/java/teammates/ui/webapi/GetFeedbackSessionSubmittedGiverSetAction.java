package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;

/**
 * Get a set of givers that has given at least one response in the feedback session.
 */
class GetFeedbackSessionSubmittedGiverSetAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

        gateKeeper.verifyAccessible(instructor, feedbackSession);
    }

    @Override
    public JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionSubmittedGiverSet output =
                new FeedbackSessionSubmittedGiverSet(
                        logic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName));
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        for (InstructorAttributes instructor : instructors) {
            if (logic.getFeedbackQuestionsForInstructors(feedbackSessionName, courseId,
                    instructor.getEmail()).isEmpty()) {
                output.getGiverIdentifiers().add(instructor.getEmail());
            }
        }

        return new JsonResult(output);
    }

}

package lycanthrope.models;

public class PlayerVoteAction {
    private String voter;
    private String previousVote;
    private int previousVotes;
    private String votedFor;
    private int votes;
    private String voteIndicator;

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public String getPreviousVote() {
        return previousVote;
    }

    public void setPreviousVote(String previousVote) {
        this.previousVote = previousVote;
    }

    public int getPreviousVotes() {
        return previousVotes;
    }

    public void setPreviousVotes(int previousVotes) {
        this.previousVotes = previousVotes;
    }

    public String getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(String votedFor) {
        this.votedFor = votedFor;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getVoteIndicator() {
        return voteIndicator;
    }

    public void setVoteIndicator(String voteIndicator) {
        this.voteIndicator = voteIndicator;
    }
}

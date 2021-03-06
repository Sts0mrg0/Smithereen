package smithereen.data;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;

import smithereen.Config;
import smithereen.activitypub.objects.Actor;
import spark.utils.StringUtils;

public class Group extends Actor{

	public int id;
	public int memberCount;

	public static Group fromResultSet(ResultSet res) throws SQLException{
		Group g;
		if(StringUtils.isNotEmpty(res.getString("domain")))
			g=new ForeignGroup();
		else
			g=new Group();
		g.fillFromResultSet(res);
		return g;
	}

	@Override
	public String getType(){
		return "Group";
	}

	@Override
	public int getLocalID(){
		return id;
	}

	@Override
	public URI getWallURL(){
		return Config.localURI("/groups/"+id+"/wall");
	}

	@Override
	public String getTypeAndIdForURL(){
		return "/groups/"+id;
	}

	@Override
	protected boolean canFollowOtherActors(){
		return false;
	}

	@Override
	protected void fillFromResultSet(ResultSet res) throws SQLException{
		super.fillFromResultSet(res);
		id=res.getInt("id");
		name=res.getString("name");
		activityPubID=Config.localURI("/groups/"+id);
		url=Config.localURI(username);
		memberCount=res.getInt("member_count");
		summary=res.getString("about");
	}

	public enum AdminLevel{
		REGULAR,
		MODERATOR,
		ADMIN,
		OWNER;

		public boolean isAtLeast(AdminLevel lvl){
			return ordinal()>=lvl.ordinal();
		}

		public boolean isAtLeast(String lvl){
			return isAtLeast(valueOf(lvl));
		}
	}

	public enum MembershipState{
		NONE,
		MEMBER,
		TENTATIVE_MEMBER,
		INVITED,
	}

	public enum Type{
		GROUP,
		EVENT
	}
}

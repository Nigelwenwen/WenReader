package com.nigel.wenreader.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.utils.RxUtils;

import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;

@Entity(tableName = "collectbooks")
public class CollectBookEntity implements Parcelable {
    private static final String TAG = "CollectBookEntity";
    /**
     * _id : 53663ae356bdc93e49004474
     * title : 逍遥派
     * author : 白马出淤泥
     * shortIntro : 金庸武侠中有不少的神秘高手，书中或提起名字，或不曾提起，总之他们要么留下了绝世秘笈，要么就名震武林。 独孤九剑的创始者，独孤求败，他真的只创出九剑吗？ 残本葵花...
     * cover : /cover/149273897447137
     * hasCp : true
     * latelyFollower : 60213
     * retentionRatio : 22.87
     * updated : 2017-05-07T18:24:34.720Z
     *
     * chaptersCount : 1660
     * lastChapter : 第1659章 朱长老
     */
    @PrimaryKey
    @NonNull
    private String _id; // 本地书籍中，path 的 md5 值作为本地书籍的 id
    private String title;
    private String author;
    private String shortIntro;
    private String cover; // 在本地书籍中，该字段作为本地文件的路径
    private boolean hasCp=false;
    private int latelyFollower=-1;
    private double retentionRatio=-1;
    private int position=-1;
    //最新更新日期
    private String updated;
    //最新阅读日期
    private String lastRead;
    private int chaptersCount=-1;
    private String lastChapter;
    //是否更新或未阅读
    private boolean isUpdate = true;
    //是否是本地文件
    private boolean isLocal = false;
    @Ignore
    private List<BookChapterEntity> bookChapterList;

    @Ignore
    public CollectBookEntity(@NonNull String _id, String title, String author, String shortIntro, String cover, boolean hasCp, int latelyFollower, double retentionRatio, int position, String updated, String lastRead, int chaptersCount, String lastChapter, boolean isUpdate, boolean isLocal, List<BookChapterEntity> bookChapterList) {
        this._id = _id;
        this.title = title;
        this.author = author;
        this.shortIntro = shortIntro;
        this.cover = cover;
        this.hasCp = hasCp;
        this.latelyFollower = latelyFollower;
        this.retentionRatio = retentionRatio;
        this.position = position;
        this.updated = updated;
        this.lastRead = lastRead;
        this.chaptersCount = chaptersCount;
        this.lastChapter = lastChapter;
        this.isUpdate = isUpdate;
        this.isLocal = isLocal;
        this.bookChapterList = bookChapterList;
    }
//
//    //本地书籍专用,room竟然是把所有基本类型都默认设为非空，很难受
//    public CollectBookEntity(@NonNull String _id, String title, String cover, int position) {
//        this._id = _id;
//        this.title = title;
//        this.cover = cover;
//        this.position = position;
//    }

    public CollectBookEntity() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShortIntro() {
        return shortIntro;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isHasCp() {
        return hasCp;
    }

    public void setHasCp(boolean hasCp) {
        this.hasCp = hasCp;
    }

    public int getLatelyFollower() {
        return latelyFollower;
    }

    public void setLatelyFollower(int latelyFollower) {
        this.latelyFollower = latelyFollower;
    }

    public double getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(double retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<BookChapterEntity> getBookChapterList() {

        if(bookChapterList==null){
            Disposable disposable=BookRepository.getInstance().getBookChaptersInRx(get_id())
                    .compose(RxUtils::toSimpleSingle)
                    .subscribe((chapters, throwable)->setBookChapterList(chapters));
            disposable.dispose();
        }
        return bookChapterList;
    }

    public void setBookChapterList(List<BookChapterEntity> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.shortIntro);
        dest.writeString(this.cover);
        dest.writeByte(this.hasCp ? (byte) 1 : (byte) 0);
        dest.writeInt(this.latelyFollower);
        dest.writeDouble(this.retentionRatio);
        dest.writeString(this.updated);
        dest.writeString(this.lastRead);
        dest.writeInt(this.chaptersCount);
        dest.writeString(this.lastChapter);
        dest.writeInt(this.position);
        dest.writeByte(this.isUpdate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocal ? (byte) 1 : (byte) 0);
    }


    protected CollectBookEntity(Parcel in) {
        this._id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.shortIntro = in.readString();
        this.cover = in.readString();
        this.hasCp = in.readByte() != 0;
        this.latelyFollower = in.readInt();
        this.retentionRatio = in.readDouble();
        this.updated = in.readString();
        this.lastRead = in.readString();
        this.chaptersCount = in.readInt();
        this.lastChapter = in.readString();
        this.position=in.readInt();
        this.isUpdate = in.readByte() != 0;
        this.isLocal = in.readByte() != 0;
    }

    public static final Creator<CollectBookEntity> CREATOR=new Creator<CollectBookEntity>(){

        @Override
        public CollectBookEntity createFromParcel(Parcel source) {
            return new CollectBookEntity(source);
        }

        @Override
        public CollectBookEntity[] newArray(int size) {
            return new CollectBookEntity[size];
        }
    };

    @Override
    public String toString() {
        return "CollectBookEntity{" +
                "_id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", shortIntro='" + shortIntro + '\'' +
                ", cover='" + cover + '\'' +
                ", hasCp=" + hasCp +
                ", latelyFollower=" + latelyFollower +
                ", retentionRatio=" + retentionRatio +
                ", position=" + position +
                ", updated='" + updated + '\'' +
                ", lastRead='" + lastRead + '\'' +
                ", chaptersCount=" + chaptersCount +
                ", lastChapter='" + lastChapter + '\'' +
                ", isUpdate=" + isUpdate +
                ", isLocal=" + isLocal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectBookEntity that = (CollectBookEntity) o;
        return Objects.equals(_id, that._id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(_id);
    }
}

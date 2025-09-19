package dao;

import application.model.entity.Comment;

import java.util.List;

public class CommentDao implements IDao<Comment>{

    @Override
    public void persist(Comment entity) {

    }

    @Override
    public Comment find(int id) {
        return null;
    }

    @Override
    public List<Comment> findAll() {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }
}

function like(btn,entityType,entityId,authorId) {
  $.post(
      CONTEXT_PATH + "/like/likeAction",
      {"entityType":entityType,"entityId":entityId,"authorId":authorId},
      function(data) {
        data = $.parseJSON(data);
        if(data.code == 0 ){
          $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
          $(btn).children("i").text(data.likeCount);
        } else{
          alert(data.msg)
        }
      }
  )
}
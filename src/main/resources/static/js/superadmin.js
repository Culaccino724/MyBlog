
var bloggerReward = "张张张LYR";

var deleteArticleId="";
var friendLinkId="";

$('.superAdminList .superAdminClick').click(function () {
    var flag = $(this).attr('class').substring(16);
    $('#statistics,#articleManagement,#articleThumbsUp,#articleCategories,#friendLink,#rewardManagement,#userFeedback,#privateWord').css("display","none");
    $("#" + flag).css("display","block");
});

//填充反馈信息
function putInAllFeedback(data) {
    var feedbackInfos = $('.feedbackInfos');
    feedbackInfos.empty();
    if(data['result'].length == 0){
        feedbackInfos.append('<div class="noNews">这里空空如也</div>');
    } else {
        $.each(data['result'], function (index, obj) {
            var feedbackInfo = $('<div class="feedbackInfo"></div>');
            feedbackInfo.append('<div class="feedbackInfoTitle">' +
                '<span class="feedbackName">' + obj['person'] + '</span>' +
                '<span class="feedbackTime">' + obj['feedbackDate'] + '</span>' +
                '</div>');
            feedbackInfo.append('<div class="feedbackInfoContent">' +
                '<span class="feedbackInfoContentWord">反馈内容：</span>' +
                obj['feedbackContent'] +
                '</div>');
            var feedbackInfoContact = $('<div class="feedbackInfoContact"></div>');
            if(obj['contactInfo'] !== ""){
                feedbackInfoContact.append('<span class="contactInfo">联系方式：</span>' +
                    obj['contactInfo']);
            } else {
                feedbackInfoContact.append('<span class="contactInfo">联系方式：</span>' + '无'
                );
            }
            feedbackInfo.append(feedbackInfoContact);
            feedbackInfos.append(feedbackInfo);
        });
        feedbackInfos.append($('<div class="my-row" id="page-father">' +
            '<div id="feedbackPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '<li class="am-disabled"><a href="">&laquo; 上一页</a></li>' +
            '<li class="am-active"><a href="">1</a></li>' +
            '<li><a href="">2</a></li>' +
            '<li><a href="">3</a></li>' +
            '<li><a href="">4</a></li>' +
            '<li><a href="">5</a></li>' +
            '<li><a href="">下一页 &raquo;</a></li>' +
            '</ul>' +
            '</div>' +
            '</div>'));
    }

}
//填充博客管理
function putInArticleManagement(data) {
    var articleManagementTable = $('.articleManagementTable');
    articleManagementTable.empty();
    var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table  am-animation-slide-right"></table>');
    table.append($('<thead>' +
        '<tr>' +
        '<th>博客标题</th><th>发布时间</th><th>博客分类</th><th>浏览量</th><th>操作</th>' +
        '</tr>' +
        '</thead>'));
    var tables = $('<tbody class="tables"></tbody>');
    $.each(data['result'], function (index, obj) {
        tables.append($('<tr id="a' + obj['id'] + '"><td><a href="article/' + obj['articleId'] + '">' + obj['articleTitle'] + '</a></td><td>' + obj['publishDate'] + '</td><td>' + obj['articleCategories'] + '</td> <td><span class="am-badge am-badge-success">' + obj['visitorNum'] + '</span></td>' +
            '<td>' +
            '<div class="am-dropdown" data-am-dropdown>' +
            '<button class="articleManagementBtn articleEditor am-btn am-btn-secondary">编辑</button>' +
            '<button class="articleDeleteBtn articleDelete am-btn am-btn-danger">删除</button>' +
            '</div>' +
            '</td>' +
            '</tr>'));
    });
    table.append(tables);
    articleManagementTable.append(table);
    articleManagementTable.append($('<div class="my-row" id="page-father">' +
        '<div id="articleManagementPagination">' +
        '<ul class="am-pagination  am-pagination-centered">' +
        '</ul>' +
        '</div>' +
        '</div>'));

    $('.articleManagementBtn').click(function () {
        var $this = $(this);
        var id = $this.parent().parent().parent().attr("id").substring(1);
        window.location.replace("/editor?id=" + id);
    });
    $('.articleDeleteBtn').click(function () {
        var $this = $(this);
        deleteArticleId = $this.parent().parent().parent().attr("id").substring(1);
        $('#deleteAlter').modal('open');
    })
}
//删除博客
$('.sureArticleDeleteBtn').click(function () {
    $.ajax({
        type:'get',
        url:'/deleteArticleBySuperadmin',
        dataType:'json',
        data:{
            id:deleteArticleId
        },
        success:function (data) {
            if(data['status'] == 201){
                dangerNotice("删除博客失败")
            } else if(data['status'] == 103){
                dangerNotice(data['message'] + " 删除博客失败")
            } else {
                successNotice("删除博客成功");
                getArticleManagement(1);
            }
        },
        error:function () {
            alert("删除失败");
        }
    });
});

//获得反馈信息
function getAllFeedback(currentPage) {
    $.ajax({
        type:'get',
        url:'/getAllFeedback',
        dataType:'json',
        data:{
            rows:10,
            pageNum:currentPage
        },
        success:function (data) {
            if(data['status'] == 103){
                dangerNotice(data['message'] + " 获得反馈失败")
            } else {
                putInAllFeedback(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#feedbackPagination").paging({
                    rows:data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum:data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages:data['data']['pageInfo']['pages'],//总页数
                    total:data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getAllFeedback(currentPage);
                    }
                });
            }
        },
        error:function () {
            alert("获取反馈信息失败");
        }
    });
}
//获取统计信息
function getStatisticsInfo() {
    $.ajax({
        type:'get',
        url:'/getStatisticsInfo',
        dataType:'json',
        data:{
        },
        success:function (data) {
            if(data['status'] == 103){
                dangerNotice(data['message'] + " 获取统计信息失败")
            } else {
                $('.allVisitor').html(data['data']['allVisitor']);
                $('.yesterdayVisitor').html(data['data']['yesterdayVisitor']);
                $('.allUser').html(data['data']['allUser']);
                $('.articleNum').html(data['data']['articleNum']);
                if(data['data']['articleThumbsUpNum'] != 0){
                    $('.articleThumbsUp').find('a').append($('<span class="am-badge am-badge-warning am-margin-right am-fr articleThumbsUpNum">' + data['data']['articleThumbsUpNum'] + '</span>'));
                }
            }
        },
        error:function () {
            alert("获取统计信息失败");
        }
    });
}
//获得博客管理博客
function getArticleManagement(currentPage) {
    $.ajax({
        type:'post',
        url:'/getArticleManagementBySuperadmin',
        dataType:'json',
        data:{
            rows:10,
            pageNum:currentPage
        },
        success:function (data) {
            if(data['status'] == 103){
                dangerNotice(data['message'] + " 获取博客失败")
            } else {
                putInArticleManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#articleManagementPagination").paging({
                    rows:data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum:data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages:data['data']['pageInfo']['pages'],//总页数
                    total:data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getArticleManagement(currentPage);
                    }
                });
            }
        },
        error:function () {
            alert("获取博客信息失败");
        }
    });
}
//获得博客分类信息
function getArticleCategories() {
    $.ajax({
        type:'get',
        url:'/getArticleCategories',
        dataType:'json',
        data:{
        },
        success:function (data) {
            if(data['status'] == 103){
                dangerNotice(data['message'] + " 获得分类失败")
            } else {
                var categoryContent = $('.categoryContent');
                categoryContent.empty();
                categoryContent.append($('<div class="contentTop">' +
                    '目前分类数：<span class="categoryNum">' + data['data']['result'].length + '</span>' +
                    '<div class="updateCategory">' +
                    '<a class="addCategory"><i class="am-icon-plus-square"></i> 添加分类</a> / ' +
                    '<a class="subCategory"><i class="am-icon-minus-square"></i> 删除分类</a>' +
                    '</div>'));
                var categories = $('<div class="categories"></div>');
                $.each(data['data']['result'], function (index, obj) {
                    categories.append($('<span id="p' + obj['id'] + '" class="category">' + obj['categoryName'] + '</span>'));
                })
                categoryContent.append(categories);

                // 添加分类
                $('.addCategory').click(function () {
                    $('#addCategory').modal({
                        relatedTarget: this,
                        onConfirm: function(e) {
                            var categoryName = e.data;
                            categoryName = $.trim(categoryName);
                            if(categoryName == ""){
                                dangerNotice("分类名不能为空！");
                            }else {
                                updateCategory(categoryName, 1);
                            }
                        },
                        onCancel: function(e) {
                        }
                    });
                })

                // 删除分类
                $('.subCategory').click(function () {
                    $('#subCategory').modal({
                        relatedTarget: this,
                        onConfirm: function(e) {
                            var categoryName = e.data;
                            categoryName = $.trim(categoryName);
                            if(categoryName == ""){
                                dangerNotice("分类名不能为空！");
                            }else {
                                updateCategory(categoryName, 2);
                            }
                        },
                        onCancel: function(e) {
                        }
                    });
                })
            }
        },
        error:function () {

        }
    });
}

// 添加或者删除分类
function updateCategory(categoryName, type) {
    $.ajax({
        type:'post',
        url:'/updateCategory',
        dataType:'json',
        data:{
            categoryName:categoryName,
            type:type
        },
        success:function (data) {
            var categoryNum = $('.categoryNum').html();
            if(data['status'] == 401){
                $('.categories').append($('<span id="p' + data['data'] + '" class="category">' + categoryName + '</span>'));
                $('.categoryNum').html(++categoryNum);
                successNotice(data['message']);
            } else if(data['status'] == 103){
                dangerNotice(data['message'] + " 更新分类失败")
            } else if (data['status'] == 402 || data['status'] == 404 || data['status'] == 405){
               dangerNotice(data['message']);
            } else if (data['status'] == 403){
                var allCategories = $('.category');
                $('.categoryNum').html(--categoryNum);
                for(var i=0;i<allCategories.length;i++){
                    if(allCategories[i].innerHTML == categoryName){
                        allCategories[i].remove();
                    }
                }
                successNotice(data['message']);
            }
        },
        error:function () {
            alert("操作失败");
        }
    });
}
//点击反馈
$('.superAdminList .userFeedback').click(function () {
    getAllFeedback(1);
});
//点击博客管理
$('.superAdminList .articleManagement').click(function () {
    getArticleManagement(1);
});
//点击分类管理
$('.superAdminList .articleCategories').click(function () {
    getArticleCategories();
});

//获得仪表盘信息
getStatisticsInfo();


//时间转换为2019年7月13日
function timestampToYMDTime(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '年';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '月';
    D = date.getDate() + '日';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds();
    return Y+M+D;
}
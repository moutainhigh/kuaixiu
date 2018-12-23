//注册序号的helper
Handlebars.registerHelper("myIndex",function(index, curPage, pageSize){
	return index + 1 + pageSize * (curPage - 1);
});

<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>pdf签名示例</title>
<script type="text/javascript" src="js/jquery-1.8.0.js"></script>
<script type="text/javascript" src="js/jquery.form.js"></script>
<script type="text/javascript">
	$(function(){
		$("#btnUpload").click(function(){
			//获取文件名
			var fileName=$("#file1").val();
			if(""==$.trim(fileName)){
				alert(请选择要上传的文件);
				return;
			}
			$("#areaUpload").wrap("<form id='myupload' action='fileupload' method='post' enctype='multipart/form-data'></form>"); 
			$("#myupload").ajaxSubmit({
					dataType:'text',
					success:function(data){
						document.getElementById('myupload').reset();
						$('#areaUpload').unwrap();
						alert(data);
						}		
					,
					error:function(){  
						$('#areaUpload').unwrap();
						alert('文件上传失败!');	
						} 
					});

		});
			
		
	})
</script>
</head>
<body>
		<div style="text-align:center;padding-top:300px;margin:0 auto " id="areaUpload">
		<span>请上传PDF文件:</span><input type="file" name="file1" id="file1"/>&nbsp;&nbsp;
		<input type="button" id="btnUpload" value="上传"/>
		</div>
</body>
</html>
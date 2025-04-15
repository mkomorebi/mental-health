
//数据接口地址列表
api_urls=[
			//"http://test.gptbot76.com/v21/",
			"https://api.gptbot78.com/v21/",//源站节点
			"https://gptapi.tkk78.com/v21/",//北京个人备案
			"https://api.aifun3.com/v21/",//企业备案
			
			"https://gptbotapi.juapp5.com/v21/",//cf
			"https://api.gptbot77.com/v21/",//改成源站，日本ecs节点1
			"https://api.gptbot76.com/v21/",//改成源站，香港轻量
		]


//通用请求api接口数据函数
//参数1：params:{'api_name':'search',
//				'api_params':{
//					'keyword':'破解','page':'1',
//					},
//				}

//参数2：回调函数对象，回调函数接收一个参数为请求结果数据
//参数3：接口中文名。例：首页获取配置信息
function r(params,method='get',api_name,call_back_func){
	//读取接口索引
	var api_index=uni.getStorageSync('api_index');
	if(String(api_index).length==0){
		api_index=0
	}else{api_index=parseInt(api_index)}
	
	//填入did，拼接成请求的data
	params['api_params']['did']=func.en_aes(uni.getStorageSync('did'))
	var p_data=params['api_params']
	console.log("当前请求的url为：",api_name,api_index,api_urls[api_index]+params['api_name'],p_data)
	//发起请求
	uni.request({
		url: api_urls[api_index]+params['api_name'],
		data:p_data,
		timeout: 10000000,
		method:method.toUpperCase(),
		dataType:'json',
		header:method.toUpperCase()=='POST'? {'content-type':'application/x-www-form-urlencoded'}:{},
		
		//数据获取成功，传递给回调函数
		success: (res) => {
			//本接口获取数据成功，把这个接口的索引保存到本地
			uni.setStorageSync('api_index',api_index)
			//数据解密
			data=func.de_aes(res.data)
			//替换{{api_url}},一些存放在源站的静态文件
			data=String(data).replace(/\{{api_url}}/g,api_urls[api_index])
			try{
				data=JSON.parse(data)
			}catch(k){
				//询问信息框
				e.a('['+api_name+']临时维护一会，请稍后再试哦！'+erorr_msg)
				return
			}
			if(data['status']=='success'){
				call_back_func(data,true)
			}else{
				//询问信息框
				e.a('['+api_name+'] > '+data['msg'])
				//call_back_func(data,false)
			}
			
		},
	
		//数据获取失败，先检测设备联网，在进行api接口域名切换
		fail: (res) => {
			//0.5秒后切换接口
			var change_api_timer = setInterval(() => {
				//关闭定时器
				if(change_api_timer!=undefined){clearInterval(change_api_timer)}
				
				//如果不是最后一个接口，就继续切换
				if(api_index<api_urls.length-1){
					//如果本地现在的接口索引和用的一样，就切换。如果不一样，就代表别的请求切换过了，直接用即可
					if(uni.getStorageSync('api_index')==api_index){
						//尝试下一个接口
						uni.setStorageSync('api_index',api_index+1)
					}
					//重新请求
					r(params,method,api_name,call_back_func)
				//所有接口尝试失败，是否重新加载
				}else{
					//接口索引置为0
					uni.setStorageSync('api_index',0)
					//询问信息框
					uni.showModal({
						title: '提示',
						content: '['+api_name+']加载失败，是否重试？'+erorr_msg,
						confirmText: "取消",cancelText: "重试",
						success: function (res) {
							if (res.cancel) 
								{r(params,method,api_name,call_back_func)}
						}
					});
				}
			}, 500);
			
		}
	});
}



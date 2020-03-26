
//用于封装网络请求

import axios from 'axios'
import {Message} from 'element-ui';

axios.interceptors.response.use(success=>{
    //如果是满足以下条件，则是业务上的错误
    if (success.status && success.status == 200 && success.data.status == 500){
        Message.error({message:success.data.msg})
        return;
    }
    return; success.data;

    },error => {  //失败的情况,满足下列条件之一
    if (error.response.status == 504 || error.response.status == 404){
        Message.error({message:'服务器被吃了'})
    }else if (error.response.status == 401){
        Message.error({message:'请先登陆！'})
    }else if (error.response.status == 403){
        Message.error({message:'权限不足，请联系管理员'})
    }else {
        if (error.response.data.msg){  //打印错误信息
            Message.error({message:error.response.data.msg})
        }else {
            Message.error({message:'未知错误！'})
        }
    }
    return;
})

let base = '';

//请求的封装
export const postKeyValueRequest=(url,params)=>{
    return axios({
        method:'post',
        url:`${base}${url}`,
        data:params,
        transformRequest:[function (data) {
            let ret = '';
            for (let i in data){
                ret += encodeURIComponent(i) + '=' + encodeURIComponent(data[i]) + '&'
            }
            console.log(ret);
            return ret;
        }],
        headers:{
            'Content-Type':'application/x-www-form-urlencoded'
        }
    })
}
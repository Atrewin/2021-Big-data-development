

#TODO 构建链接
import os
import time, datetime
from boto3.session import Session
import  boto3
import  hashlib
# Client init
access_key = "3C792CB222BEAAE59195"
secret_key = "WzhEMEI2RUVDNDY0MjE0MDM1REQxRjJDNzExODE4"
url = "http://scut.depts.bingosoft.net:29997"

session  = Session(access_key, secret_key)
s3_client = session.client("s3", endpoint_url=url )

# TODO 下载文件

class Downloader:


    def __init__(self):

        self.bucket_name = "jinhui"
        self.part_size = 5 * 1024 * 1024
        self.startusemultpartsize = 4  # MB
        self.client_store_rootpath = "D:/project/python/workSpace/Course/2021-Big-data-development/大数据怎么存/testdata"

        pass

    def downloadfiles(self):
        # 列出当前的文件Bucket的所有文件
        store_path = self.client_store_rootpath
        bucket_name = self.bucket_name
        resp = s3_client.list_objects(Bucket=bucket_name)  # 文件夹和文件都会有url,
        for obj in resp["Contents"]:
            cfilePath = os.path.join(store_path, obj["Key"])
            # 判断是否为具体文件
            # if not cfilePath[-1] in ["/", "\\"]:
            if cfilePath[-1] in ["/", "\\"]:
                if not os.path.exists(cfilePath):
                    os.mkdir(cfilePath)  # 调用系统命令来创建文件
            else:
                # 判断本地文件情况
                if not os.path.exists(cfilePath):
                    with open(cfilePath, 'wb') as f:
                        file_object = s3_client.get_object(Bucket=bucket_name, Key=obj["Key"])
                        f.write(file_object['Body'].read())
                        print("download ", obj["Key"], " from ", bucket_name)
                else:
                    # 对比文件信息, 选择保存
                    file_object_metadata = s3_client.head_object(Bucket=bucket_name, Key=obj["Key"])

                    c_mtime = os.path.getmtime(cfilePath)
                    s_mtime = time.mktime(file_object_metadata["LastModified"].timetuple())
                    if s_mtime > c_mtime:
                        with open(cfilePath, 'wb') as f:
                            file_object = s3_client.get_object(Bucket=bucket_name, Key=obj["Key"])
                            f.write(file_object['Body'].read())
                            print("update ", cfilePath, " with ", bucket_name, " file")




class Uploader:
    def __init__(self):
        # self.client_root_path = r"C:\app\eclipse\work-space\testdata"
        self.client_root_path = "D:/project/python/workSpace/Course/2021-Big-data-development/大数据怎么存/testdata"
        self.bucket_name = "jinhui"
        self.part_size = 5*1024*1024
        self.startusemultpartsize = 4# MB
        pass

    def upload_folder(self):
        # 获取当前文件所有的文件
        urlList = []
        # 删除多余的文件
        self.delete_exfiles(self.bucket_name, self.client_root_path)
        self.get_all_url(root_path=self.client_root_path, urlList=urlList)
        # 把本地文件提交上去
        self.uploadfiles(urlList)
        print("finish upload files to ", self.bucket_name)
        pass

    def get_all_url(self, root_path, urlList):

        for file in os.listdir(root_path):
            file_path = os.path.join(root_path, file)
            if os.path.isdir(file_path):
                # urlList.append(file_path)
                self.get_all_url(file_path,urlList)
                # 不知道文件夹要不要保留上传
                pass
            else:
                urlList.append(file_path)

        pass

    def uploadfiles(self, urlList):

        for url in urlList:
            # 取得和Bucket的相对路径
            s_file_url = url.replace(self.client_root_path, "")
            if s_file_url[0] in ["/", "\\"]:
                s_file_url = s_file_url[1:].replace("\\", "/")

            #TODO 执行上传策略
            #上传本地文件

            try:
                # 判断服务器是否存在
                file_object_metadata = s3_client.head_object(Bucket=self.bucket_name, Key=s_file_url)
                c_md5 = self.calculate_md5(url)
                ETag = file_object_metadata["ETag"].replace("\"", "")# 分块的不知道ETag是怎么算的.
                if c_md5 == ETag:#没有发生更新 file_object_metadata["ETag"]API有问题
                    continue
                # 分块上传的是否MD5不知道怎么算的,所以选着新策略
                file_object_metadata = s3_client.head_object(Bucket=self.bucket_name, Key=s_file_url)
                c_mtime = os.path.getmtime(url)
                s_mtime = time.mktime(file_object_metadata["LastModified"].timetuple())
                if s_mtime == c_mtime:
                    continue
            except:
                pass
            # 上传
            # 判断文件大小,选择上传策略
            filesize = self.getfile_size(url)
            if filesize >= self.startusemultpartsize:
                self.multipart_upload(url, s_file_url)
                pass
            else:
                body = open(url,"rb").read()
                resp = s3_client.put_object(Bucket=self.bucket_name, Key=s_file_url, Body=body, StorageClass="STANDARD")

    def multipart_upload(self, url, s_file_url):

        uploaded_id = self.get_uploaded_Id(self.bucket_name, s_file_url)

        if uploaded_id == None:
            print("start multipart upload ", url)
            self.new_multipart_upload(url,s_file_url)
        else:# 续传
            print("recover upload ", url," to Bucket ", self.bucket_name)
            self.recover_upload(url,s_file_url,uploaded_id)
            print("finished recover upload ", url)

    def calculate_md5(self, file):
        with open(file, "rb",) as f:
            # 计算的方式需要和远端的同步, 针对分块模式下的计算方式
            # 判断文件大小,选择上传策略
            filesize = self.getfile_size(file)
            if filesize >= self.startusemultpartsize:
                # 分块计算的方式
                i = 1
                md5 = hashlib.md5()

                while 1:
                    bytes = f.read(self.part_size)
                    if bytes == b"":
                        break
                    md5.update(bytes)# 配合分块上传
                    i += 1
                md5 = md5.hexdigest() + "-" + str(i-1)
                pass

            else:
                bytes = f.read()
                md5 = hashlib.md5(bytes).hexdigest();



        return md5

    def delete_exfiles(self, bucket_name, c_root):
        wait_delete_urls = []
        resp = s3_client.list_objects(Bucket=bucket_name)  # 文件夹和文件都会有url,
        for obj in resp["Contents"]:
            cfilePath = os.path.join(c_root, obj["Key"])
            # 判断是否为具体文件
            if cfilePath[-1] in ["/", "\\"]:
                continue
            else:
                # 判断本地文件情况
                if not os.path.exists(cfilePath):
                    wait_delete_urls.append({"Key":obj["Key"]})
                    print("server: delete ", obj["Key"])
                else:
                    continue
        s3_client.delete_objects(
            Bucket=bucket_name,
            Delete={
                "Objects":wait_delete_urls
            }
        )

    def getfile_size(self,file_url):
        filesize = os.path.getsize(file_url)
        filesize = filesize/float(1024*1024)
        return round(filesize,2)

    def recover_upload(self, url, s_file_url, uploadId):
        # 判断对应块是否已经上传
        response = s3_client.list_parts(Bucket=self.bucket_name, Key=s_file_url, UploadId=uploadId)
        if "Parts" in response.keys():
            miss_parts = response["Parts"]
            error = False
            with open(url, "rb", ) as f:
                for part in miss_parts:
                    ETag = part["ETag"]
                    partNumber = part["PartNumber"]
                    partSize = part["Size"]
                    offset = (partNumber - 1) * self.part_size
                    f.seek(offset, 0)
                    data = f.read(partSize)
                    print("upload the ", partNumber, " part")

                    response = s3_client.upload_part(Bucket=self.bucket_name,
                                                     Key=s_file_url, PartNumber=partNumber,
                                                     UploadId=uploadId, Body=data)
                    #  未处理问题, 当文件有再次修改后需要怎么处理呢? 如果发现有ETag 不一致,需要全方位重传
                    # 如果发现有块对不上,意味着文件发生过改变,需要重新上传
                    if ETag != response["ETag"]:
                        s3_client.abort_multipart_upload(Bucket=self.bucket_name, Key=s_file_url, UploadId=uploadId)
                        error = True
                        break
            if error:
                self.new_multipart_upload(url, s_file_url)
                # 已上传完毕,拼接parts
                # 远端应该是自己会监控,当全部part 到了后会启动合并
            #  未处理问题, 当文件有再次修改后需要怎么处理呢? 如果发现有ETag 不一致,需要全方位重传

        pass
    def new_multipart_upload(self, url, s_file_url):
        #
        mpu = s3_client.create_multipart_upload(Bucket=self.bucket_name, Key=s_file_url,
                                                StorageClass="STANDARD")  # UploadId 是怎么用的 m每次不一样的, 用于标识一次上传,如果记住可以实现断点续传
        part_info = {  # 用来拿到对应part上传得到的反馈信息, 建立起远端收到的和自己发送的对应关系,当全部都收到返回后,将对应的关系发给服务器,进行拼接(这需要client来触发,因为s它不知道什么时候结束)
            "Parts": []
        }
        part_size = self.part_size
        i = 1
        threads = []
        with open(url, "rb", ) as f:
            while 1:
                data = f.read(part_size)
                if data == b"":
                    break
                print("uploading the ", i, " part")
                # 这部分需要多线程异步
                t = threading.Thread(target=self.upload_part, args=(self.bucket_name, s_file_url, i, mpu,data, part_info))
                threads.append(t)
                t.start()
                # 异步处理
                i += 1
            # 然后对应的块还不一定上传成功了, 只是告诉系统最后成功的目标是什么, 需要判断才知道是否成功
            for t in threads:
                t.join()
            s3_client.complete_multipart_upload(Bucket=self.bucket_name, Key=s_file_url, UploadId=mpu["UploadId"],
                                                MultipartUpload=part_info)  # step3.完成上传
            try:
                s3_client.list_parts(Bucket=self.bucket_name, Key=s_file_url, UploadId=mpu["UploadId"])
                print("fail to upload")
            except:
                print("seccessed to upload ", url)
                pass

    def get_uploaded_Id(self, bucket_name, s_file_url):

        response = s3_client.list_multipart_uploads(Bucket=bucket_name, Prefix=s_file_url)

        if u"Uploads" in response.keys():
            return response[u"Uploads"][-1][u"UploadId"] if len(response[u"Uploads"]) > 0 else None
        else:
            return None

    def upload_part(self, bucket_name, s_file_url, partNumber, mpu, data, part_info):
        response = s3_client.upload_part(Bucket=bucket_name,
                                         Key=s_file_url, PartNumber=partNumber,
                                         UploadId=mpu["UploadId"], Body=data)  # step2.上传分片 #可改用多线程
        part_info['Parts'].append({  # 这个是否需要等上传成功的时候才可以拿到呢?
            'PartNumber': partNumber,
            'ETag': response['ETag']
        })

    def clear_storage(self):
        response = s3_client.list_multipart_uploads(Bucket=self.bucket_name)
        if u"Uploads" in response.keys():
            for Upload in response[u"Uploads"]:
                UploadId = Upload[u"UploadId"]
                Key = Upload["Key"]
                s3_client.abort_multipart_upload(Bucket=self.bucket_name, Key=Key, UploadId=UploadId)
        print("finished clear multipart temporary storage")
        pass




downloader_header = Downloader()
downloader_header.downloadfiles()
upload_header = Uploader()
upload_header.upload_folder()



print("#"*9)







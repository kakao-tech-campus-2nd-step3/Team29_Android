package com.iguana.domain.usecase

import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.repository.AIRepository
import javax.inject.Inject

class GetAIResultByPageUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    operator fun invoke(documentId:Long, pageNumber:Int): Result<AIResult> {
        // TODO : 서버 완료되면 해당 내용 주석 해제
//        return aiRepository.getSummarizationByPage(documentId, pageNumber)
        // 임시 결과 반환
        return Result.success(
            AIResult(
                documentId = documentId,
                pageNumber = pageNumber,
                summary = """
            <p>이 내용은 <b>GPU 렌더링 파이프라인</b>과 <b>변환(Transformation)</b>에 대한 설명입니다. GPU 파이프라인은 여러 단계로 이루어져 있으며, <b>Vertex Shader, Rasterizer, Fragment Shader, Output Merger</b>로 구성됩니다. 이 중 <b>Shader</b> 부분은 개발자가 프로그래밍할 수 있으며, 나머지는 GPU 내에서 자동으로 처리됩니다. Vertex Shader는 3D 데이터를 2D로 변환하는 중요한 역할을 합니다.</p>
            
            <h4>주요 개념:</h4>
            <ol>
                <li><b><span style="color:red;">GPU 렌더링 파이프라인</span></b>: 3D 데이터를 2D 이미지로 변환하는 과정을 담당. 주요 단계는 <b>Vertex Shader</b>, <b>Rasterizer</b>, <b>Fragment Shader</b>, <b>Output Merger</b>. <b>Vertex Shader</b>는 오브젝트를 <b>Object Space → World Space → Camera Space</b>로 변환.</li>
                <li><b>Scaling, Rotation, Translation</b>: 
                    <ul>
                        <li><b>Scaling</b>: 물체의 크기 변환.</li>
                        <li><b>Rotation</b>: 기본적으로 반시계 방향으로 물체 회전.</li>
                        <li><b>Translation</b>: 물체를 좌표계에서 이동시키는 것. <b>동차 좌표계(Homogeneous Coordinates)</b>를 사용하여 행렬곱으로 변환 가능.</li>
                    </ul>
                </li>
                <li><b>Affine Transform</b>: 크기, 회전, 이동 변환을 포함한 변환. 행렬의 마지막 열이 (0, 0, 1)이면 아핀 변환이다.</li>
            </ol>
        """.trimIndent(),
                problem = """
            <ol>
                <li>GPU 렌더링 파이프라인의 각 단계를 나열하고, 개발자가 프로그래밍할 수 있는 부분과 GPU 내부에서 자동으로 처리되는 부분을 구분하시오.</li>
                <p><b>답:</b><br/>
                <ul>
                    <b><span style="color:red;">Vertex Shader</span></b> (개발자 프로그래밍 가능)</li>
                    <li><b>Rasterizer</b> (GPU 자동 처리)</li>
                    <li><b>Fragment Shader</b> (개발자 프로그래밍 가능)</li>
                    <li><b>Output Merger</b> (GPU 자동 처리)</li>
                </ul>
                </p>
                <li>동차 좌표계(Homogeneous Coordinates)의 목적과 예시를 설명하시오.</li>
                <p><b>답:</b><br/>
                <ul>
                    <li><b>목적</b>: 이동 변환(Translation)을 행렬 곱셈으로 표현하기 위해 사용한다.</li>
                    <li><b>예시</b>: (2, 3) 좌표를 동차 좌표계로 변환하면 (2, 3, 1)로 변환된다. 이를 통해 이동 변환도 행렬 곱으로 표현 가능.</li>
                </ul>
                </p>
            </ol>
        """.trimIndent()
            )
        )

    }
}